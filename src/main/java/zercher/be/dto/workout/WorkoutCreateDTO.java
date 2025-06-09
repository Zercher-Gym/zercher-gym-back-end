package zercher.be.dto.workout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCreateDTO implements Serializable {
    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Set<WorkoutLabelCreateDTO> labels = new HashSet<>();

    @NotNull
    private Set<WorkoutExerciseCreateUpdateDTO> exercises = new HashSet<>();
}
