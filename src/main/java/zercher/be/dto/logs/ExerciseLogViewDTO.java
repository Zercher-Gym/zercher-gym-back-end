package zercher.be.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLogViewDTO implements Serializable {
    private Long workoutExerciseId;

    private Long customWorkoutExerciseId;

    private String details;
}
