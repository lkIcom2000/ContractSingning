package dk.au.credentialgeneration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response containing generated credentials")
public class CredentialResponse {
    @Schema(description = "Generated username", example = "john.doe")
    private String username;

    @Schema(description = "Generated password", example = "bapetiku")
    private String password;

    @Schema(description = "Hashed password for storage", example = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy")
    private String hashedPassword;
} 