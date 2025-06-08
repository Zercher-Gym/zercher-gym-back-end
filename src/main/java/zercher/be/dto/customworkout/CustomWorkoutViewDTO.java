package zercher.be.dto.customworkout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.customexercise.CustomWorkoutCustomExerciseViewDTO;
import zercher.be.dto.exercise.CustomWorkoutExerciseViewDTO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomWorkoutViewDTO implements Serializable {
    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    private Set<CustomWorkoutExerciseViewDTO> exercises = new HashSet<>();

    @NotNull
    private Set<CustomWorkoutCustomExerciseViewDTO> customExercises = new HashSet<>();
}
