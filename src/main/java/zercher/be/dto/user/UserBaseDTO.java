package zercher.be.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserBaseDTO implements Serializable {
    @NotBlank
    @Size(max = 40)
    protected String username;

    @NotBlank
    @Size(max = 40)
    protected String password;
}
