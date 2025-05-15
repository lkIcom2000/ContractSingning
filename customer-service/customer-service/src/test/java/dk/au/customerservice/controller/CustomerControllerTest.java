package dk.au.customerservice.controller;

import dk.au.customerservice.dto.CustomerDTO;
import dk.au.customerservice.model.Customer;
import dk.au.customerservice.service.CustomerService;
import dk.au.customerservice.utils.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {
    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerController customerController;
    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;
    private Map<String, String> testCredentials;

    @BeforeEach
    void setUp() {
        customerController = new CustomerController(customerService, customerMapper);

        testCredentials = new HashMap<>();
        testCredentials.put("username", "max.mustermann");
        testCredentials.put("password", "mysecret");

        testCustomer = new Customer(
            "Max Mustermann",
            "2025-05-15",
            "Birk Centerpark 120",
            "1234567",
            testCredentials
        );

        testCustomerDTO = new CustomerDTO(
            "Max Mustermann",
            "2025-05-15",
            "Birk Centerpark 120",
            "1234567",
            testCredentials
        );
    }

    @Test
    void getCustomerById_WhenCustomerExists_ShouldReturnCustomer() {
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(testCustomer.getName());
        assertThat(response.getBody().getCredentials()).isEqualTo(testCustomer.getCredentials());
        verify(customerService).getCustomerById(1L);
        verify(customerMapper).toDTO(testCustomer);
    }

    @Test
    void getCustomerById_WhenCustomerDoesNotExist_ShouldReturn404() {
        when(customerService.getCustomerById(1L)).thenReturn(Optional.empty());

        ResponseEntity<CustomerDTO> response = customerController.getCustomerById(1L);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        verify(customerService).getCustomerById(1L);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        when(customerMapper.toEntity(testCustomerDTO)).thenReturn(testCustomer);
        when(customerService.createCustomer(testCustomer)).thenReturn(testCustomer);
        when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

        ResponseEntity<CustomerDTO> response = customerController.createCustomer(testCustomerDTO);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(testCustomer.getName());
        assertThat(response.getBody().getCredentials()).isEqualTo(testCustomer.getCredentials());
        verify(customerMapper).toEntity(testCustomerDTO);
        verify(customerService).createCustomer(testCustomer);
        verify(customerMapper).toDTO(testCustomer);
    }
} 