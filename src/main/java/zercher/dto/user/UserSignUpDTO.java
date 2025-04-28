package zercher.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserSignUpDTO extends UserBaseDTO implements Serializable {
    @NotBlank
    @Email
    private String email;
}
