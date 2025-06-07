package zercher.be.dto.customexercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.unit.UnitViewDTO;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomWorkoutCustomExerciseViewDTO implements Serializable {
    @NotNull
    private Long id;

    @NotNull
    private UUID customExerciseId;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    private UnitViewDTO unit;

    @NotNull
    private Integer quantity;
}
