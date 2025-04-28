package zercher.dto.user;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserViewDTO extends UserBaseDTO implements Serializable {
    @NotBlank
    private UUID id;

    @NotBlank
    private Set<String> roles;
}
