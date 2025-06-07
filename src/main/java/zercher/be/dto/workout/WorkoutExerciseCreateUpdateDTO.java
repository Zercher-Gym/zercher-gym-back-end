package zercher.be.dto.workout;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseCreateUpdateDTO implements Serializable {
    @NotNull
    private UUID exerciseId;

    @NotNull
    private Long unitId;

    @NotNull
    private Integer quantity;
}
