package zercher.be.dto.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.workoutlabel.WorkoutLabelViewDTO;
import zercher.be.model.enums.Language;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutViewListDTO implements Serializable {
    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Map<Language, WorkoutLabelViewDTO> labels;
}
