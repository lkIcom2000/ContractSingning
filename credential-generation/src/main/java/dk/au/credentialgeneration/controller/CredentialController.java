package dk.au.credentialgeneration.controller;

import dk.au.credentialgeneration.dto.CredentialRequest;
import dk.au.credentialgeneration.dto.CredentialResponse;
import dk.au.credentialgeneration.dto.PasswordVerificationRequest;
import dk.au.credentialgeneration.utils.PasswordGenerator;
import dk.au.credentialgeneration.utils.PasswordHasher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
@Tag(name = "Credential Management", description = "APIs for generating and verifying credentials")
public class CredentialController {
    private final PasswordGenerator passwordGenerator;
    private final PasswordHasher passwordHasher;
    private final WebClient webClient;

    @PostMapping("/generate")
    @Operation(summary = "Generate credentials for a customer", description = "Generates a username based on the customer's name and a pronounceable password")
    public Mono<ResponseEntity<CredentialResponse>> generateCredentials(@RequestBody CredentialRequest request) {
        String username = passwordGenerator.generateUsername(request.getFullName());
        String password = passwordGenerator.generatePronounceablePassword(8);
        String hashedPassword = passwordHasher.hashPassword(password);

        // Create credentials map
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", hashedPassword);

        // Create request body for customer-service
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("credentials", credentials);

        // Call customer-service to update credentials
        return webClient.patch()
                .uri("/api/customers/{id}/credentials", request.getCustomerId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .retrieve()
                .toBodilessEntity()
                .map(response -> {
                    CredentialResponse credentialResponse = new CredentialResponse();
                    credentialResponse.setUsername(username);
                    credentialResponse.setPassword(password);
                    credentialResponse.setHashedPassword(hashedPassword);
                    return ResponseEntity.ok(credentialResponse);
                });
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify a password", description = "Verifies if a raw password matches the stored password for a customer")
    public Mono<ResponseEntity<Boolean>> verifyPassword(@RequestBody PasswordVerificationRequest request) {
        // First, get the customer's credentials from customer-service
        return webClient.get()
                .uri("/api/customers/{id}", request.getCustomerId())
                .retrieve()
                .bodyToMono(Map.class)
                .map(customerData -> {
                    Map<String, Object> customer = (Map<String, Object>) customerData;
                    Map<String, String> credentials = (Map<String, String>) customer.get("credentials");
                    
                    if (credentials == null || !credentials.containsKey("password")) {
                        return ResponseEntity.ok(false);
                    }

                    String storedHashedPassword = credentials.get("password");
                    boolean isValid = passwordHasher.verifyPassword(request.getRawPassword(), storedHashedPassword);
                    return ResponseEntity.ok(isValid);
                })
                .onErrorReturn(ResponseEntity.ok(false));
    }
} 