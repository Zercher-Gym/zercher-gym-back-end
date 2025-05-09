package zercher.be.dto.exerciselabel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.model.enums.Language;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLabelCreateDTO implements Serializable {
    @NotNull
    private Language language;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;
}
