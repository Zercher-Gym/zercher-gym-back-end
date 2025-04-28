package zercher.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserBaseDTO implements Serializable {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
