package dk.au.credentialgeneration.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@Slf4j
public class PasswordGenerator {
    private static final String[] VOWELS = {"a", "e", "i", "o", "u"};
    private static final String[] CONSONANTS = {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "x", "y", "z"};
    private static final Random RANDOM = new Random();

    public PasswordGenerator() {
        log.info("PasswordGenerator initialized with {} vowels and {} consonants", 
                VOWELS.length, CONSONANTS.length);
    }

    public String generatePronounceablePassword(int length) {
        if (length <= 0) {
            log.error("Invalid password length requested: {}", length);
            throw new IllegalArgumentException("Password length must be positive");
        }
        
        log.debug("Generating pronounceable password of length: {}", length);
        
        StringBuilder password = new StringBuilder();
        boolean useVowel = RANDOM.nextBoolean();

        while (password.length() < length) {
            if (useVowel) {
                password.append(VOWELS[RANDOM.nextInt(VOWELS.length)]);
            } else {
                password.append(CONSONANTS[RANDOM.nextInt(CONSONANTS.length)]);
            }
            useVowel = !useVowel;
        }

        // Ensure the password is exactly the requested length
        String generatedPassword = password.substring(0, length);
        log.debug("Successfully generated pronounceable password of length: {}", generatedPassword.length());
        
        return generatedPassword;
    }

    public String generateUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            log.error("Attempted to generate username with null or empty full name");
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }

        log.debug("Generating username for full name: {}", fullName);

        String[] nameParts = fullName.toLowerCase().split("\\s+");
        if (nameParts.length < 2) {
            log.error("Full name must contain at least two parts, received: {}", fullName);
            throw new IllegalArgumentException("Full name must contain at least two parts");
        }

        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];

        // Generate username as firstname.lastname
        String username = firstName + "." + lastName;
        log.info("Generated username: {} for full name: {}", username, fullName);
        
        return username;
    }
} 