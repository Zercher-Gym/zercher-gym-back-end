package zercher.be.dto.customexercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomExerciseCreateDTO {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    private Long unitId;
}
