package zercher.be.dto.logs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.workout.WorkoutLabelViewDTO;
import zercher.be.model.enums.Language;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogViewListDTO implements Serializable {
    @NotNull
    private UUID id;

    @NotNull
    private Date createdAt;

    private String title;

    private String description;

    private Map<Language, WorkoutLabelViewDTO> labels;
}
