package zercher.controller;

import zercher.dto.user.UserSignInDTO;
import zercher.dto.user.UserSignUpDTO;
import zercher.service.authentication.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody UserSignInDTO userForSignInDto) {
        String token = authenticationService.signIn(userForSignInDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserSignUpDTO userForSignUpDto) {
        String token = authenticationService.signUp(userForSignUpDto);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}