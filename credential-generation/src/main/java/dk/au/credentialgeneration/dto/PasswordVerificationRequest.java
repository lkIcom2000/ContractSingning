package dk.au.credentialgeneration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request for verifying a password")
public class PasswordVerificationRequest {
    @Schema(description = "ID of the customer", example = "1")
    private Long customerId;

    @Schema(description = "Raw password to verify", example = "bapetiku")
    private String rawPassword;

    @Schema(description = "Hashed password to compare against", example = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
    private String hashedPassword;
} 