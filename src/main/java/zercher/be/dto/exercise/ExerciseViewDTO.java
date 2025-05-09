package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.exerciselabel.ExerciseLabelViewDTO;
import zercher.be.model.enums.Language;

import java.io.Serializable;
import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseViewDTO implements Serializable {
    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private HashMap<Language, ExerciseLabelViewDTO> labels;
}
