package zercher.be.dto.exercise;

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
public class ExerciseSearchAdminDTO implements Serializable {
    @Size(max = 100)
    private String identifier;

    private Language language;

    @Size(max = 100)
    private String title;

    @Size(max = 255)
    private String description;
}
