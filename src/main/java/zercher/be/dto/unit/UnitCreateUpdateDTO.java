package zercher.be.dto.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zercher.be.model.enums.UnitType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitCreateUpdateDTO {
    @NotBlank
    private String code;

    @NotNull
    private UnitType type;
}
