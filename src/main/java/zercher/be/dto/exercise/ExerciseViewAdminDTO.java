package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.unit.UnitViewDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseViewAdminDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Set<ExerciseLabelViewAdminDTO> labels = new HashSet<>();

    @NotNull
    private Set<UnitViewDTO> units = new HashSet<>();
}
