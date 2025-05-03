package zercher.be.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserSignUpDTO implements Serializable {
    @NotBlank
    @Size(max = 40)
    private String username;

    @NotBlank
    @Size(max = 40)
    private String password;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;
}
