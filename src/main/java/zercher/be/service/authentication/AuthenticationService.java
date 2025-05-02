package zercher.be.service.authentication;

import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;

public interface AuthenticationService {
    String signIn(UserSignInDTO userSignInDTO);

    void signUp(UserSignUpDTO userSignUpDTO);
}