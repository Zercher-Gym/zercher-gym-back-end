package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import zercher.be.dto.exerciselabel.ExerciseLabelCreateDTO;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCreateDTO implements Serializable {
    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Set<ExerciseLabelCreateDTO> labels;
}
