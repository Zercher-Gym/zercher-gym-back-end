package zercher.be.dto.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogViewDTO extends WorkoutLogViewListDTO {
    private UUID workoutId;

    private UUID customWorkoutId;

    private String details;

    private List<ExerciseLogViewDTO> exerciseLogs = new ArrayList<>();
}
