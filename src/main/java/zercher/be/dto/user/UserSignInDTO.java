package zercher.be.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class UserSignInDTO extends UserBaseDTO implements Serializable {
}
