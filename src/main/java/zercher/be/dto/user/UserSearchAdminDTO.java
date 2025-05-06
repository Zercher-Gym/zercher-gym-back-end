package zercher.be.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchAdminDTO implements Serializable {
    @Size(max = 40)
    private String username;

    @Size(max = 255)
    private String email;

    private UUID id;

    private Boolean enabled;

    private Boolean locked;
}
