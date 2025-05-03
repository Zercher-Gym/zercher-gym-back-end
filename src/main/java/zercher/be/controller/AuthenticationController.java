package zercher.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;
import zercher.be.response.BaseResponse;
import zercher.be.service.authentication.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/signin")
    public ResponseEntity<BaseResponse<String>> authenticateUser(@Valid @RequestBody UserSignInDTO userSignInDTO) {
        String token = authenticationService.signIn(userSignInDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true, null, token));
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> createUser(@Valid @RequestBody UserSignUpDTO userSignUpDTO) {
        authenticationService.signUp(userSignUpDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));

    }

    @PostMapping("/confirmEmail/{token}")
    public ResponseEntity<BaseResponse<Void>> confirmEmail(@PathVariable UUID token) {
        authenticationService.confirmEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}