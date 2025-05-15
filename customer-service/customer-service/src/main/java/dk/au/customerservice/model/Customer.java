package dk.au.customerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public Customer(String name, String birth, String adress, String phoneNumber) {
        this.name = name;
        this.birth = birth;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
    }
} 