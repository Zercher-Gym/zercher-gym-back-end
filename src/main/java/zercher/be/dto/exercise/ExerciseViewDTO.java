package zercher.be.dto.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.dto.unit.UnitViewDTO;
import zercher.be.model.enums.Language;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseViewDTO implements Serializable {
    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String identifier;

    @NotNull
    private Map<Language, ExerciseLabelViewDTO> labels = new HashMap<>();

    @NotNull
    private Set<UnitViewDTO> units = new HashSet<>();
}
