package zercher.be.service.authentication;

import zercher.be.dto.user.UserEmailDTO;
import zercher.be.dto.user.UserNewPasswordDTO;
import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;

import java.util.UUID;

public interface AuthenticationService {
    String signIn(UserSignInDTO userSignInDTO);

    String signInAdmin(UserSignInDTO userSignInDTO);

    void signUp(UserSignUpDTO userSignUpDTO);

    void confirmEmail(UUID token);

    void sendConfirmEmail(UserEmailDTO emailDTO);

    void resetPassword(UUID token, UserNewPasswordDTO newPasswordDTO);

    void sendResetPassword(UserEmailDTO emailDTO);
}