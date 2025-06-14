package zercher.be.dto.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLabelViewDTO {
    @NotNull
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
