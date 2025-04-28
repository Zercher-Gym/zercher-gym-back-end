package zercher.dto.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserSignInDTO extends UserBaseDTO implements Serializable {
}
