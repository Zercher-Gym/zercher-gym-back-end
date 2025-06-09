package zercher.be.dto.workout;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLabelViewDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
