package dk.au.credentialgeneration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://customer-service:8080")
                .filter((request, next) -> {
                    System.out.println("Making request to: " + request.url());
                    return next.exchange(request);
                })
                .build();
    }
} 