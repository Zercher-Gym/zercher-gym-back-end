package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ExerciseLabelViewAdminDTO implements Serializable {
    @NotNull
    private Long id;

    @NotNull
    private Language language;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
