package zercher.be.controller;

import org.springframework.web.bind.annotation.*;
import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;
import zercher.be.service.authentication.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody UserSignInDTO userSignInDTO) {
        String token = authenticationService.signIn(userSignInDTO);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserSignUpDTO userSignUpDTO) {
        authenticationService.signUp(userSignUpDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/confirmEmail/{token}")
    public ResponseEntity<Void> confirmEmail(@PathVariable UUID token) {
        authenticationService.confirmEmail(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}