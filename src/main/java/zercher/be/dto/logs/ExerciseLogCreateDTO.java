package zercher.be.dto.logs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLogCreateDTO implements Serializable {
    private Long workoutExerciseId;

    private Long customWorkoutExerciseId;

    private String details;

    @NotNull
    private List<String> detailsList = new ArrayList<>();
}
