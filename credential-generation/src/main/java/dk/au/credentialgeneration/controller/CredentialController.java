package dk.au.credentialgeneration.controller;

import dk.au.credentialgeneration.dto.CredentialRequest;
import dk.au.credentialgeneration.dto.CredentialResponse;
import dk.au.credentialgeneration.dto.CredentialUpdateRequest;
import dk.au.credentialgeneration.dto.CustomerDTO;
import dk.au.credentialgeneration.dto.PasswordVerificationRequest;
import dk.au.credentialgeneration.utils.PasswordGenerator;
import dk.au.credentialgeneration.utils.PasswordHasher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Credential Management", description = "APIs for generating and verifying credentials")
public class CredentialController {
    private final PasswordGenerator passwordGenerator;
    private final PasswordHasher passwordHasher;
    private final WebClient webClient;

    @PostMapping("/generate")
    @Operation(summary = "Generate credentials for a customer", description = "Generates a username based on the customer's name and a pronounceable password")
    public Mono<ResponseEntity<CredentialResponse>> generateCredentials(@RequestBody CredentialRequest request) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        log.info("Starting credential generation request - RequestId: {}, CustomerId: {}", 
                requestId, request.getCustomerId());
        
        // First, get the customer's name from customer-service
        return webClient.get()
                .uri("/api/customers/{id}", request.getCustomerId())
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> log.debug("Customer service response received - RequestId: {}, Response: {}", 
                        requestId, response))
                .doOnError(error -> log.error("Failed to retrieve customer data - RequestId: {}, CustomerId: {}, Error: {}", 
                        requestId, request.getCustomerId(), error.getMessage(), error))
                .flatMap(customerData -> {
                    Map<String, Object> customer = (Map<String, Object>) customerData;
                    String customerName = (String) customer.get("name");
                    
                    log.info("Customer data retrieved successfully - RequestId: {}, CustomerName: {}", 
                            requestId, customerName);
                    
                    // Generate credentials using the customer's name
                    String username = passwordGenerator.generateUsername(customerName);
                    String password = passwordGenerator.generatePronounceablePassword(8);
                    String hashedPassword = passwordHasher.hashPassword(password);

                    log.info("Credentials generated - RequestId: {}, Username: {}, PasswordLength: {}", 
                            requestId, username, password.length());
                    log.debug("Generated password hash - RequestId: {}, HashPrefix: {}", 
                            requestId, hashedPassword.substring(0, Math.min(10, hashedPassword.length())) + "...");

                    // Create credentials map
                    Map<String, String> credentials = new HashMap<>();
                    credentials.put("username", username);
                    credentials.put("password", hashedPassword);

                    // Create proper request object for customer-service
                    CredentialUpdateRequest updateRequest = new CredentialUpdateRequest(credentials);

                    log.debug("Sending credential update request - RequestId: {}, UpdateRequest: {}", 
                            requestId, updateRequest);

                    // Update the customer's credentials
                    return webClient.patch()
                            .uri("/api/customers/{id}/credentials", request.getCustomerId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(updateRequest)
                            .retrieve()
                            .bodyToMono(CustomerDTO.class)
                            .doOnNext(response -> log.info("Credentials updated successfully - RequestId: {}, Response: {}", 
                                    requestId, response))
                            .doOnError(error -> log.error("Failed to update customer credentials - RequestId: {}, CustomerId: {}, Error: {}", 
                                    requestId, request.getCustomerId(), error.getMessage(), error))
                            .map(response -> {
                                log.info("Credential generation completed successfully - RequestId: {}, CustomerId: {}", 
                                        requestId, request.getCustomerId());
                                CredentialResponse credentialResponse = new CredentialResponse();
                                credentialResponse.setUsername(username);
                                credentialResponse.setPassword(password);
                                credentialResponse.setHashedPassword(hashedPassword);
                                return ResponseEntity.ok(credentialResponse);
                            });
                })
                .onErrorResume(e -> {
                    log.error("Credential generation failed - RequestId: {}, CustomerId: {}, Error: {}", 
                            requestId, request.getCustomerId(), e.getMessage(), e);
                    return Mono.just(ResponseEntity.<CredentialResponse>status(500).build());
                });
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify a password", description = "Verifies if a raw password matches the stored password for a customer")
    public Mono<ResponseEntity<Boolean>> verifyPassword(@RequestBody PasswordVerificationRequest request) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        log.info("Starting password verification - RequestId: {}, CustomerId: {}", 
                requestId, request.getCustomerId());
        
        // First, get the customer's credentials from customer-service
        return webClient.get()
                .uri("/api/customers/{id}", request.getCustomerId())
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> log.debug("Customer data retrieved for verification - RequestId: {}", requestId))
                .doOnError(error -> log.error("Failed to retrieve customer for password verification - RequestId: {}, CustomerId: {}, Error: {}", 
                        requestId, request.getCustomerId(), error.getMessage(), error))
                .map(customerData -> {
                    Map<String, Object> customer = (Map<String, Object>) customerData;
                    Map<String, String> credentials = (Map<String, String>) customer.get("credentials");
                    
                    if (credentials == null || !credentials.containsKey("password")) {
                        log.warn("No credentials found for customer - RequestId: {}, CustomerId: {}", 
                                requestId, request.getCustomerId());
                        return ResponseEntity.ok(false);
                    }

                    String storedHashedPassword = credentials.get("password");
                    boolean isValid = passwordHasher.verifyPassword(request.getRawPassword(), storedHashedPassword);
                    
                    log.info("Password verification completed - RequestId: {}, CustomerId: {}, IsValid: {}", 
                            requestId, request.getCustomerId(), isValid);
                    
                    if (!isValid) {
                        log.warn("Invalid password attempt - RequestId: {}, CustomerId: {}", 
                                requestId, request.getCustomerId());
                    }
                    
                    return ResponseEntity.ok(isValid);
                })
                .onErrorResume(error -> {
                    log.error("Password verification failed - RequestId: {}, CustomerId: {}, Error: {}", 
                            requestId, request.getCustomerId(), error.getMessage(), error);
                    return Mono.just(ResponseEntity.ok(false));
                });
    }
} 