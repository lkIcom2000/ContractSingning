package dk.au.customerservice.controller;

import dk.au.customerservice.dto.CustomerDTO;
import dk.au.customerservice.dto.CredentialUpdateRequest;
import dk.au.customerservice.model.Customer;
import dk.au.customerservice.service.CustomerService;
import dk.au.customerservice.utils.CustomerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "APIs for managing customer information")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("Getting all customers");
        List<CustomerDTO> customers = customerService.getAllCustomers()
                .stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Retrieved {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        log.info("Getting customer by ID: {}", id);
        Optional<Customer> customer = customerService.getCustomerById(id);
        if (customer.isPresent()) {
            log.info("Customer found: {}", customer.get().getName());
            return ResponseEntity.ok(customerMapper.toDTO(customer.get()));
        } else {
            log.warn("Customer with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        log.info("Creating new customer: {}", customerDTO.getName());
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer created = customerService.createCustomer(customer);
        log.info("Customer created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(customerMapper.toDTO(created));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID: {}", id);
        customerService.deleteCustomer(id);
        log.info("Customer with ID {} deleted successfully", id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/credentials")
    @Operation(summary = "Update customer credentials", description = "Updates the credentials for a specific customer")
    public ResponseEntity<CustomerDTO> updateCustomerCredentials(
            @PathVariable Long id,
            @RequestBody CredentialUpdateRequest request) {
        log.info("Updating credentials for customer ID: {}", id);
        log.debug("Credential update request: {}", request);
        log.debug("New credentials keys: {}", request.getCredentials() != null ? request.getCredentials().keySet() : "null");
        
        Optional<Customer> updatedCustomer = customerService.updateCustomerCredentials(id, request.getCredentials());
        
        if (updatedCustomer.isPresent()) {
            log.info("Customer credentials updated successfully: {}", updatedCustomer.get().getName());
            return ResponseEntity.ok(customerMapper.toDTO(updatedCustomer.get()));
        } else {
            log.warn("Customer with ID {} not found for credential update", id);
            return ResponseEntity.notFound().build();
        }
    }
} 