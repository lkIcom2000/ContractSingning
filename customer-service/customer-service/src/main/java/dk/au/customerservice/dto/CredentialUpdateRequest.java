package dk.au.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Request for updating customer credentials")
public class CredentialUpdateRequest {
    @Schema(description = "New credentials to update", example = "{\"username\": \"new.username\", \"password\": \"newhashedpassword\"}")
    private Map<String, String> credentials;
} 