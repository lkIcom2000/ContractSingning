package dk.au.customerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String birth;
    private String adress;
    private String phoneNumber;
    
    @ElementCollection
    @CollectionTable(name = "customer_credentials", 
                    joinColumns = @JoinColumn(name = "customer_id"))
    @MapKeyColumn(name = "credential_key")
    @Column(name = "credential_value")
    private Map<String, String> credentials = new HashMap<>();

    public Customer(String name, String birth, String adress, String phoneNumber) {
        this.name = name;
        this.birth = birth;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.credentials = new HashMap<>();
    }

    public Customer(String name, String birth, String adress, String phoneNumber, Map<String, String> credentials) {
        this.name = name;
        this.birth = birth;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.credentials = credentials != null ? credentials : new HashMap<>();
    }
} 