package dk.au.exhibitionservice.repository;

import dk.au.exhibitionservice.model.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    List<Exhibition> findByCategory(String category);
    List<Exhibition> findByDate(LocalDate date);
    List<Exhibition> findByCategoryAndDate(String category, LocalDate date);
} 