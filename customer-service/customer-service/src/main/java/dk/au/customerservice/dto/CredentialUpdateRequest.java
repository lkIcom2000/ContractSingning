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
@Schema(description = "Request for updating customer credentials")
public class CredentialUpdateRequest {
    @Schema(description = "New credentials to update", example = "{\"username\": \"new.username\", \"password\": \"newhashedpassword\"}")
    private Map<String, String> credentials = new HashMap<>();
} 