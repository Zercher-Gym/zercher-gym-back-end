package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLabelViewDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
