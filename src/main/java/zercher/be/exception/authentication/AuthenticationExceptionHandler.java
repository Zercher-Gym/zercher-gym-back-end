package zercher.be.exception.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zercher.be.response.BaseResponse;

@Slf4j
@RestControllerAdvice
public class AuthenticationExceptionHandler {
    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse<Void>> handleUserAuthenticationException(AuthenticationException ignoredException) {
        log.error("User authentication failed!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>("userAuthenticationFailed"));
    }

    @ExceptionHandler({DisabledException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse<Void>> handleUserNotEnabledException(DisabledException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>("userNotEnabled"));
    }

    @ExceptionHandler({BadCredentialsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse<Void>> handleInvalidCredentialsException(BadCredentialsException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>("invalidCredentials"));
    }

    @ExceptionHandler({ExpiredVerificationTokenException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse<Void>> handleExpiredVerificationTokenException(ExpiredVerificationTokenException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>("expiredVerificationToken"));
    }

    @ExceptionHandler({InvalidTokenTypeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse<Void>> handleExpiredVerificationTokenException(InvalidTokenTypeException ignoredException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>("invalidVerificationTokenType"));
    }
}
