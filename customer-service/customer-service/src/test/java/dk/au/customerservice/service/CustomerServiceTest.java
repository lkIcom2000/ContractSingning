package dk.au.customerservice.service;

import dk.au.customerservice.model.Customer;
import dk.au.customerservice.repo.CustomerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepo customerRepo;

    private CustomerService customerService;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepo);
        testCustomer = new Customer(
            "Max Mustermann",
            "2025-05-15",
            "Birk Centerpark 120",
            "1234567"
        );
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        when(customerRepo.findAll()).thenReturn(Arrays.asList(testCustomer));

        List<Customer> customers = customerService.getAllCustomers();

        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getName()).isEqualTo(testCustomer.getName());
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(testCustomer));

        Optional<Customer> found = customerService.getCustomerById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(testCustomer.getName());
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        when(customerRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> found = customerService.getCustomerById(1L);

        assertThat(found).isEmpty();
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        when(customerRepo.save(any(Customer.class))).thenReturn(testCustomer);

        Customer created = customerService.createCustomer(testCustomer);

        assertThat(created.getName()).isEqualTo(testCustomer.getName());
        verify(customerRepo).save(testCustomer);
    }

    @Test
    void deleteCustomer_ShouldCallRepository() {
        customerService.deleteCustomer(1L);
        verify(customerRepo).deleteById(1L);
    }
} 