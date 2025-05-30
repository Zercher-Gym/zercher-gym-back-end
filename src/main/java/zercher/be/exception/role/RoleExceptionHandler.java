package zercher.be.exception.role;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zercher.be.response.BaseResponse;

@Slf4j
@RestControllerAdvice
public class RoleExceptionHandler {
    @ExceptionHandler({RoleLimitExceeded.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse<Void>> handleRoleLimitExceededException(RoleLimitExceeded exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(exception.getMessage()));
    }
}
