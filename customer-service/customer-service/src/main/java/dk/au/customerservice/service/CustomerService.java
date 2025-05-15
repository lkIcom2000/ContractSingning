package dk.au.customerservice.service;

import dk.au.customerservice.model.Customer;
import dk.au.customerservice.repo.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepo customerRepo;

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepo.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepo.deleteById(id);
    }

    public Optional<Customer> updateCustomerCredentials(Long id, Map<String, String> newCredentials) {
        return customerRepo.findById(id)
                .map(customer -> {
                    customer.setCredentials(newCredentials);
                    return customerRepo.save(customer);
                });
    }
} 