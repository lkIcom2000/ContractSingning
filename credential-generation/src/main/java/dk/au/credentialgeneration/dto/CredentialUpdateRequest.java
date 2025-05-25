package dk.au.credentialgeneration.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CredentialUpdateRequest {
    private Map<String, String> credentials;
    
    public CredentialUpdateRequest(Map<String, String> credentials) {
        this.credentials = credentials;
    }
} 