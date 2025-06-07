package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.exerciselabel.ExerciseLabelViewDTO;
import zercher.be.dto.unit.UnitViewDTO;
import zercher.be.model.enums.Language;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseViewDTO implements Serializable {
    @NotNull
    private Long id;

    @NotNull
    private UUID exerciseId;

    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Map<Language, ExerciseLabelViewDTO> labels = new HashMap<>();

    @NotNull
    private UnitViewDTO unit;

    @NotNull
    private Integer quantity;
}
