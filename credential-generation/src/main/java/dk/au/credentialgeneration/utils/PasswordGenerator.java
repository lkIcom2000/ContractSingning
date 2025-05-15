package dk.au.credentialgeneration.utils;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class PasswordGenerator {
    private static final String[] VOWELS = {"a", "e", "i", "o", "u"};
    private static final String[] CONSONANTS = {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "x", "y", "z"};
    private static final Random RANDOM = new Random();

    public String generatePronounceablePassword(int length) {
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
        return password.substring(0, length);
    }

    public String generateUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }

        String[] nameParts = fullName.toLowerCase().split("\\s+");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full name must contain at least two parts");
        }

        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];

        // Generate username as firstname.lastname
        return firstName + "." + lastName;
    }
} 