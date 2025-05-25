package dk.au.credentialgeneration.dto;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String birth;
    private String adress;
    private String phoneNumber;
    private Map<String, String> credentials = new HashMap<>();
} 