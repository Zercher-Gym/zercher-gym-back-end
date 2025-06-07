package zercher.be.dto.workout;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSearchDTO implements Serializable {
    @Size(max = 100)
    private String contains;

    @NotNull
    private Integer limit;
}
