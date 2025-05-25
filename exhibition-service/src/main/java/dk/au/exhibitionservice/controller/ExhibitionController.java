

import dk.au.exhibitionservice.dto.ExhibitionDTO;
import dk.au.exhibitionservice.model.Exhibition;
import dk.au.exhibitionservice.service.ExhibitionService;
import dk.au.exhibitionservice.utils.ExhibitionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exhibitions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exhibition Management", description = "APIs for managing exhibition information")
public class ExhibitionController {
    private final ExhibitionService exhibitionService;
    private final ExhibitionMapper exhibitionMapper;

    @GetMapping
    @Operation(summary = "Get all exhibitions")
    public ResponseEntity<List<ExhibitionDTO>> getAllExhibitions() {
        log.info("Getting all exhibitions");
        List<ExhibitionDTO> exhibitions = exhibitionService.getAllExhibitions()
                .stream()
                .map(exhibitionMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Retrieved {} exhibitions", exhibitions.size());
        return ResponseEntity.ok(exhibitions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exhibition by ID")
    public ResponseEntity<ExhibitionDTO> getExhibitionById(@PathVariable Long id) {
        log.info("Getting exhibition by ID: {}", id);
        Optional<Exhibition> exhibition = exhibitionService.getExhibitionById(id);
        if (exhibition.isPresent()) {
            log.info("Exhibition found: {}", exhibition.get().getCategory());
            return ResponseEntity.ok(exhibitionMapper.toDTO(exhibition.get()));
        } else {
            log.warn("Exhibition with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get exhibitions by category")
    public ResponseEntity<List<ExhibitionDTO>> getExhibitionsByCategory(@PathVariable String category) {
        log.info("Getting exhibitions by category: {}", category);
        List<ExhibitionDTO> exhibitions = exhibitionService.getExhibitionsByCategory(category)
                .stream()
                .map(exhibitionMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Retrieved {} exhibitions for category: {}", exhibitions.size(), category);
        return ResponseEntity.ok(exhibitions);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get exhibitions by date")
    public ResponseEntity<List<ExhibitionDTO>> getExhibitionsByDate(@PathVariable LocalDate date) {
        log.info("Getting exhibitions by date: {}", date);
        List<ExhibitionDTO> exhibitions = exhibitionService.getExhibitionsByDate(date)
                .stream()
                .map(exhibitionMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Retrieved {} exhibitions for date: {}", exhibitions.size(), date);
        return ResponseEntity.ok(exhibitions);
    }

    @PostMapping
    @Operation(summary = "Create a new exhibition")
    public ResponseEntity<ExhibitionDTO> createExhibition(@RequestBody ExhibitionDTO exhibitionDTO) {
        log.info("Creating new exhibition: {}", exhibitionDTO.getCategory());
        Exhibition exhibition = exhibitionMapper.toEntity(exhibitionDTO);
        Exhibition created = exhibitionService.createExhibition(exhibition);
        log.info("Exhibition created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(exhibitionMapper.toDTO(created));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an exhibition")
    public ResponseEntity<ExhibitionDTO> updateExhibition(
            @PathVariable Long id,
            @RequestBody ExhibitionDTO exhibitionDTO) {
        log.info("Updating exhibition with ID: {}", id);
        Exhibition exhibitionUpdate = exhibitionMapper.toEntity(exhibitionDTO);
        Optional<Exhibition> updatedExhibition = exhibitionService.updateExhibition(id, exhibitionUpdate);
        
        if (updatedExhibition.isPresent()) {
            log.info("Exhibition updated successfully: {}", updatedExhibition.get().getCategory());
            return ResponseEntity.ok(exhibitionMapper.toDTO(updatedExhibition.get()));
        } else {
            log.warn("Exhibition with ID {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exhibition")
    public ResponseEntity<Void> deleteExhibition(@PathVariable Long id) {
        log.info("Deleting exhibition with ID: {}", id);
        boolean deleted = exhibitionService.deleteExhibition(id);
        if (deleted) {
            log.info("Exhibition with ID {} deleted successfully", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("Exhibition with ID {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{exhibitionId}/customers/{customerId}")
    @Operation(summary = "Add customer to exhibition")
    public ResponseEntity<ExhibitionDTO> addCustomerToExhibition(
            @PathVariable Long exhibitionId,
            @PathVariable Long customerId) {
        log.info("Adding customer {} to exhibition {}", customerId, exhibitionId);
        Optional<Exhibition> updatedExhibition = exhibitionService.addCustomerToExhibition(exhibitionId, customerId);
        
        if (updatedExhibition.isPresent()) {
            log.info("Customer {} added to exhibition {} successfully", customerId, exhibitionId);
            return ResponseEntity.ok(exhibitionMapper.toDTO(updatedExhibition.get()));
        } else {
            log.warn("Exhibition with ID {} not found", exhibitionId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{exhibitionId}/customers/{customerId}")
    @Operation(summary = "Remove customer from exhibition")
    public ResponseEntity<ExhibitionDTO> removeCustomerFromExhibition(
            @PathVariable Long exhibitionId,
            @PathVariable Long customerId) {
        log.info("Removing customer {} from exhibition {}", customerId, exhibitionId);
        Optional<Exhibition> updatedExhibition = exhibitionService.removeCustomerFromExhibition(exhibitionId, customerId);
        
        if (updatedExhibition.isPresent()) {
            log.info("Customer {} removed from exhibition {} successfully", customerId, exhibitionId);
            return ResponseEntity.ok(exhibitionMapper.toDTO(updatedExhibition.get()));
        } else {
            log.warn("Exhibition with ID {} not found", exhibitionId);
            return ResponseEntity.notFound().build();
        }
    }
} 