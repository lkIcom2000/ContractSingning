package dk.au.customerservice.controller;

import dk.au.customerservice.dto.CustomerDTO;
import dk.au.customerservice.dto.CredentialUpdateRequest;
import dk.au.customerservice.model.Customer;
import dk.au.customerservice.service.CustomerService;
import dk.au.customerservice.utils.CustomerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customer information")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers()
                .stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(c -> ResponseEntity.ok(customerMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.ok(customerMapper.toDTO(created));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/credentials")
    @Operation(summary = "Update customer credentials", description = "Updates the credentials for a specific customer")
    public ResponseEntity<CustomerDTO> updateCustomerCredentials(
            @PathVariable Long id,
            @RequestBody CredentialUpdateRequest request) {
        Optional<Customer> updatedCustomer = customerService.updateCustomerCredentials(id, request.getCredentials());
        return updatedCustomer
                .map(customer -> ResponseEntity.ok(customerMapper.toDTO(customer)))
                .orElse(ResponseEntity.notFound().build());
    }
} 