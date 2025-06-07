package zercher.be.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleLimitViewDTO implements Serializable {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    private Integer exerciseLimit;

    private Integer workoutLimit;
}
