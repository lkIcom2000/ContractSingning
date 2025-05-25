package dk.au.exhibitionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exhibition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate date;
    
    private String category;
    
    @ElementCollection
    @CollectionTable(name = "exhibition_customers", 
                    joinColumns = @JoinColumn(name = "exhibition_id"))
    @Column(name = "customer_id")
    private List<Long> customerIds = new ArrayList<>();

    public Exhibition(LocalDate date, String category) {
        this.date = date;
        this.category = category;
        this.customerIds = new ArrayList<>();
    }

    public Exhibition(LocalDate date, String category, List<Long> customerIds) {
        this.date = date;
        this.category = category;
        this.customerIds = customerIds != null ? customerIds : new ArrayList<>();
    }
} 