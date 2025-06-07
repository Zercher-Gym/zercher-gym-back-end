package zercher.be.dto.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.exercise.WorkoutExerciseViewDTO;
import zercher.be.dto.workoutlabel.WorkoutLabelViewDTO;
import zercher.be.model.enums.Language;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutViewDTO implements Serializable {
    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Map<Language, WorkoutLabelViewDTO> labels = new HashMap<>();

    @NotNull
    private Set<WorkoutExerciseViewDTO> exercises = new HashSet<>();
}
