package zercher.be.service.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;
import zercher.be.exception.authentication.ExpiredVerificationTokenException;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.model.User;
import zercher.be.model.VerificationToken;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.VerificationTokenRepository;
import zercher.be.security.Role;
import zercher.be.security.token.TokenUtilities;
import zercher.be.service.mail.MailService;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${com.zercher.be.email.verify}")
    private Boolean verifyEmail;

    @Value("${com.zercher.be.email.tokenExpirationMs}")
    private long verificationTokenExpirationTimeMs;

    @Value("${com.zercher.fe.emailConfirmationUrl}")
    private String emailConfirmationUrl;

    private final MailService mailService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final TokenUtilities tokenUtilities;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String signIn(UserSignInDTO userSignInDTO) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userSignInDTO.getUsername(), userSignInDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var user = (User) authentication.getPrincipal();

        return tokenUtilities.generateToken(user);
    }

    @Override
    public void signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.existsByUsername(userSignUpDTO.getUsername())) {
            throw new ResourceExistsException("userWithUsernameExists");
        }

        if (userRepository.existsByEmail(userSignUpDTO.getEmail())) {
            throw new ResourceExistsException("userWithEmailExists");
        }

        var role = roleRepository.findByName(Role.USER.getName())
                .orElseThrow(() -> new ResourceNotFoundException("roleNotFound"));

        var user = User.builder()
                .username(userSignUpDTO.getUsername())
                .email(userSignUpDTO.getEmail())
                .password(passwordEncoder.encode(userSignUpDTO.getPassword()))
                .roles(Collections.singleton(role))
                .enabled(!verifyEmail)
                .locked(false)
                .build();

        userRepository.save(user);

        log.info("Created new user {}!", user.getUsername());

        if (verifyEmail) {
            var tokenExpiration = new Date(new Date().getTime() + verificationTokenExpirationTimeMs);
            var token = new VerificationToken(null, user, tokenExpiration);
            verificationTokenRepository.save(token);

            var confirmationUrl = emailConfirmationUrl + token.getId();

            var context = new Context();
            context.setVariable("username", userSignUpDTO.getUsername());
            context.setVariable("confirmationUrl", confirmationUrl);

            mailService.sendEmail(user.getEmail(), "confirmEmail.subject", "confirmEmail", context);
        }
    }

    @Override
    public void confirmEmail(UUID token) {
        var verificationToken = verificationTokenRepository.findById(token)
                .orElseThrow(() -> new ResourceNotFoundException("verificationTokenNotFound"));
        if (verificationToken.getExpiryDate().before(new Date())) {
            throw new ExpiredVerificationTokenException();
        }

        var user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        log.info("Enabled user {}!", user.getUsername());

        var tokens = verificationTokenRepository.getByUser(user);
        verificationTokenRepository.deleteAll(tokens);

        log.info("Cleaned up verification tokens for user {}!", user.getUsername());
    }
}
