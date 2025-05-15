package dk.au.credentialgeneration.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {
    private final BCryptPasswordEncoder encoder;

    public PasswordHasher() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
} 