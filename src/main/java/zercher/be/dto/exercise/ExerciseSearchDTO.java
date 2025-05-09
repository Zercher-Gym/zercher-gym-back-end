package zercher.be.dto.exercise;

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
public class ExerciseSearchDTO implements Serializable {
    @Size(max = 100)
    private String contains;

    private Integer limit;
}
