package zercher.be.dto.customexercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomExerciseViewDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String description;
}
