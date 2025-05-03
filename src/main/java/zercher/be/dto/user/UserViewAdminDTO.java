package zercher.be.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserViewAdminDTO implements Serializable {
    @NotBlank
    @Size(max = 40)
    private String username;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    private UUID id;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean locked;

    @NotBlank
    private Date createdAt;

    @NotBlank
    private Set<String> roles;
}
