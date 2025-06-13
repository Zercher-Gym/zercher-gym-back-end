package zercher.be.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogCreateDTO implements Serializable {
    private UUID workoutId;

    private UUID customWorkoutId;

    private String details;

    private List<ExerciseLogCreateDTO> exercises = new ArrayList<>();
}
