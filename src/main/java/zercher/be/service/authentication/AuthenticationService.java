package zercher.be.service.authentication;

import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;

import java.util.UUID;

public interface AuthenticationService {
    String signIn(UserSignInDTO userSignInDTO);

    void signUp(UserSignUpDTO userSignUpDTO);

    void confirmEmail(UUID token);
}