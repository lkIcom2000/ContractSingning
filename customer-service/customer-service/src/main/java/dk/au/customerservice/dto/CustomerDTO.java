package dk.au.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Customer information")
public class CustomerDTO {
    @Schema(description = "ID of the customer")
    private Long id;

    @Schema(description = "Name of the customer", example = "Max Mustermann")
    private String name;

    @Schema(description = "Birth date of the customer", example = "2025-05-15")
    private String birth;

    @Schema(description = "Address of the customer", example = "Birk Centerpark 120")
    private String adress;

    @Schema(description = "Phone number of the customer", example = "1234567")
    private String phoneNumber;

    @Schema(description = "Credentials of the customer", example = "{\"username\": \"max.mustermann\", \"password\": \"mysecret\"}")
    private Map<String, String> credentials = new HashMap<>();

    public CustomerDTO(String name, String birth, String adress, String phoneNumber) {
        this.name = name;
        this.birth = birth;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.credentials = new HashMap<>();
    }

    public CustomerDTO(String name, String birth, String adress, String phoneNumber, Map<String, String> credentials) {
        this.name = name;
        this.birth = birth;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.credentials = credentials != null ? credentials : new HashMap<>();
    }
} 