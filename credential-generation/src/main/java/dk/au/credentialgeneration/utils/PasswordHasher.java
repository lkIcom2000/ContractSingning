package dk.au.credentialgeneration.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PasswordHasher {
    private final BCryptPasswordEncoder encoder;

    public PasswordHasher() {
        this.encoder = new BCryptPasswordEncoder();
        log.info("PasswordHasher initialized with BCrypt encoder");
    }

    public String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            log.error("Attempted to hash null or empty password");
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        log.debug("Hashing password of length: {}", password.length());
        String hashedPassword = encoder.encode(password);
        log.debug("Password hashed successfully, hash length: {}", hashedPassword.length());
        
        return hashedPassword;
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            log.error("Attempted to verify with null password or hash");
            return false;
        }
        
        log.debug("Verifying password of length: {} against hash of length: {}", 
                rawPassword.length(), hashedPassword.length());
        
        boolean matches = encoder.matches(rawPassword, hashedPassword);
        log.debug("Password verification result: {}", matches);
        
        return matches;
    }
} 

