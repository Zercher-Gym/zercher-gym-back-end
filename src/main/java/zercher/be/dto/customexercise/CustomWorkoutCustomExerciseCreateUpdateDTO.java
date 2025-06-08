package zercher.be.dto.customexercise;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomWorkoutCustomExerciseCreateUpdateDTO {
    @NotNull
    private UUID customExerciseId;

    @NotNull
    private Long unitId;

    @NotNull
    private Integer quantity;
}
