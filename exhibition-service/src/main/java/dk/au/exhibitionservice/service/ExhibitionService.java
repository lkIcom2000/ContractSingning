package dk.au.exhibitionservice.service;

import dk.au.exhibitionservice.model.Exhibition;
import dk.au.exhibitionservice.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitionService {
    private final ExhibitionRepository exhibitionRepository;

    public List<Exhibition> getAllExhibitions() {
        log.debug("Getting all exhibitions");
        return exhibitionRepository.findAll();
    }

    public Optional<Exhibition> getExhibitionById(Long id) {
        log.debug("Getting exhibition by ID: {}", id);
        return exhibitionRepository.findById(id);
    }

    public List<Exhibition> getExhibitionsByCategory(String category) {
        log.debug("Getting exhibitions by category: {}", category);
        return exhibitionRepository.findByCategory(category);
    }

    public List<Exhibition> getExhibitionsByDate(LocalDate date) {
        log.debug("Getting exhibitions by date: {}", date);
        return exhibitionRepository.findByDate(date);
    }

    public Exhibition createExhibition(Exhibition exhibition) {
        log.debug("Creating exhibition: {}", exhibition);
        Exhibition savedExhibition = exhibitionRepository.save(exhibition);
        log.info("Exhibition created with ID: {}", savedExhibition.getId());
        return savedExhibition;
    }

    public Optional<Exhibition> updateExhibition(Long id, Exhibition exhibitionUpdate) {
        log.debug("Updating exhibition with ID: {}", id);
        return exhibitionRepository.findById(id)
                .map(existingExhibition -> {
                    if (exhibitionUpdate.getDate() != null) {
                        existingExhibition.setDate(exhibitionUpdate.getDate());
                    }
                    if (exhibitionUpdate.getCategory() != null) {
                        existingExhibition.setCategory(exhibitionUpdate.getCategory());
                    }
                    if (exhibitionUpdate.getCustomerIds() != null) {
                        existingExhibition.setCustomerIds(exhibitionUpdate.getCustomerIds());
                    }
                    Exhibition updated = exhibitionRepository.save(existingExhibition);
                    log.info("Exhibition updated with ID: {}", updated.getId());
                    return updated;
                });
    }

    public boolean deleteExhibition(Long id) {
        log.debug("Deleting exhibition with ID: {}", id);
        if (exhibitionRepository.existsById(id)) {
            exhibitionRepository.deleteById(id);
            log.info("Exhibition deleted with ID: {}", id);
            return true;
        }
        log.warn("Exhibition with ID {} not found for deletion", id);
        return false;
    }

    public Optional<Exhibition> addCustomerToExhibition(Long exhibitionId, Long customerId) {
        log.debug("Adding customer {} to exhibition {}", customerId, exhibitionId);
        return exhibitionRepository.findById(exhibitionId)
                .map(exhibition -> {
                    if (!exhibition.getCustomerIds().contains(customerId)) {
                        exhibition.getCustomerIds().add(customerId);
                        Exhibition updated = exhibitionRepository.save(exhibition);
                        log.info("Customer {} added to exhibition {}", customerId, exhibitionId);
                        return updated;
                    }
                    log.warn("Customer {} already exists in exhibition {}", customerId, exhibitionId);
                    return exhibition;
                });
    }

    public Optional<Exhibition> removeCustomerFromExhibition(Long exhibitionId, Long customerId) {
        log.debug("Removing customer {} from exhibition {}", customerId, exhibitionId);
        return exhibitionRepository.findById(exhibitionId)
                .map(exhibition -> {
                    if (exhibition.getCustomerIds().remove(customerId)) {
                        Exhibition updated = exhibitionRepository.save(exhibition);
                        log.info("Customer {} removed from exhibition {}", customerId, exhibitionId);
                        return updated;
                    }
                    log.warn("Customer {} not found in exhibition {}", customerId, exhibitionId);
                    return exhibition;
                });
    }
} 