package zercher.be.dto.logs;

import lombok.*;

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
