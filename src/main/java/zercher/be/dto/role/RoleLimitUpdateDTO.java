package zercher.be.dto.role;

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
public class RoleLimitUpdateDTO implements Serializable {
    @NotNull
    private Integer exerciseLimit;
}
