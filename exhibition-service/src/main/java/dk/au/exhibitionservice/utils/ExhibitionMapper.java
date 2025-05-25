package dk.au.exhibitionservice.utils;

import dk.au.exhibitionservice.dto.ExhibitionDTO;
import dk.au.exhibitionservice.model.Exhibition;
import org.springframework.stereotype.Component;

@Component
public class ExhibitionMapper {

    public ExhibitionDTO toDTO(Exhibition exhibition) {
        return new ExhibitionDTO(
                exhibition.getId(),
                exhibition.getDate(),
                exhibition.getCategory(),
                exhibition.getCustomerIds()
        );
    }

    public Exhibition toEntity(ExhibitionDTO dto) {
        return new Exhibition(
                dto.getDate(),
                dto.getCategory(),
                dto.getCustomerIds()
        );
    }
} 