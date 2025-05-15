package dk.au.credentialgeneration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request for generating credentials")
public class CredentialRequest {
    @Schema(description = "Full name of the customer", example = "John Doe")
    private String fullName;

    @Schema(description = "ID of the customer", example = "1")
    private Long customerId;
} 