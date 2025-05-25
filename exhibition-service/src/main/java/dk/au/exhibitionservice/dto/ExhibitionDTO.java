

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionDTO {
    private Long id;
    private LocalDate date;
    private String category;
    private List<Long> customerIds;
    private Stand[] stands;
} 