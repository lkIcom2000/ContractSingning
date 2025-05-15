package dk.au.credentialgeneration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "dk.au.credentialgeneration.model")
@EnableJpaRepositories(basePackages = "dk.au.credentialgeneration.repo")
public class CredentialGenerationApplication {
    public static void main(String[] args) {
        SpringApplication.run(CredentialGenerationApplication.class, args);
    }
} 