package zercher.be.exception.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthenticationExceptionHandler {
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleUserAuthenticationException(AuthenticationException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userAuthenticationFailed");
    }

    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<String> handleUserNotEnabledException(DisabledException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userNotEnabled");
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<String> handleInvalidCredentialsException(BadCredentialsException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalidCredentials");
    }
}
