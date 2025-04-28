package zercher.service.authentication;

import zercher.dto.user.UserSignInDTO;
import zercher.dto.user.UserSignUpDTO;

public interface AuthenticationService {
    String signIn(UserSignInDTO userSignInDTO);

    String signUp(UserSignUpDTO userForSignUpDto);
}