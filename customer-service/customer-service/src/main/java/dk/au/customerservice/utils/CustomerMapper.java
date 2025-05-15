package dk.au.customerservice.utils;

import dk.au.customerservice.dto.CustomerDTO;
import dk.au.customerservice.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
            customer.getId(),
            customer.getName(),
            customer.getBirth(),
            customer.getAdress(),
            customer.getPhoneNumber()
        );
    }

    public Customer toEntity(CustomerDTO dto) {
        return new Customer(
            dto.getName(),
            dto.getBirth(),
            dto.getAdress(),
            dto.getPhoneNumber()
        );
    }
} 