package dk.au.customerservice.config;

import dk.au.customerservice.model.Customer;
import dk.au.customerservice.repo.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    
    private final CustomerRepo customerRepo;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (customerRepo.count() == 0) {
            System.out.println("Loading test customer data...");
            
            // Create test customer
            Customer testCustomer = new Customer(
                "Max Mustermann",
                "1990-05-15",
                "Birk Centerpark 120",
                "1234567890"
            );
            
            customerRepo.save(testCustomer);
            System.out.println("Test customer created with ID: " + testCustomer.getId());
        } else {
            System.out.println("Customer data already exists, skipping initialization.");
        }
    }
} 