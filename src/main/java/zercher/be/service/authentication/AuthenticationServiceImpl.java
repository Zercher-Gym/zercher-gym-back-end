package zercher.be.service.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import zercher.be.dto.user.UserEmailDTO;
import zercher.be.dto.user.UserNewPasswordDTO;
import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;
import zercher.be.exception.authentication.ExpiredVerificationTokenException;
import zercher.be.exception.authentication.InvalidTokenTypeException;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.model.entity.User;
import zercher.be.model.entity.VerificationToken;
import zercher.be.model.enums.VerificationTokenType;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.VerificationTokenRepository;
import zercher.be.model.enums.Role;
import zercher.be.security.token.TokenUtilities;
import zercher.be.service.mail.MailService;

import java.util.ArrayList;
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

    @Value("${com.zercher.fe.confirmEmailUrl}")
    private String confirmEmailUrl;

    @Value("${com.zercher.fe.resetPasswordUrl}")
    private String resetPasswordUrl;

    private final MailService mailService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final TokenUtilities tokenUtilities;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String signIn(UserSignInDTO userSignInDTO) {
        var user = signInUser(userSignInDTO);
        return tokenUtilities.generateToken(user);
    }

    @Override
    public String signInAdmin(UserSignInDTO userSignInDTO) {
        try {
            var user = signInUser(userSignInDTO);

            var requiredRoles = new ArrayList<Role>() {{
                add(Role.ADMIN);
                add(Role.MODERATOR);
            }};

            for (var role : requiredRoles) {
                var roleEntity = roleRepository.findByName(role.getName()).orElseThrow(
                        () -> new ResourceNotFoundException("roleNotFound")
                );
                if (!user.getRoles().contains(roleEntity)) {
                    throw new AuthorizationDeniedException("accessDenied");
                }
            }

            return tokenUtilities.generateToken(user);
        } catch (Exception exception) {
            throw new BadCredentialsException("invalidCredentials");
        }
    }

    private User signInUser(UserSignInDTO userSignInDTO) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userSignInDTO.getUsername(), userSignInDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return (User) authentication.getPrincipal();
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
            this.generateEmailConfirmationToken(user);
        }
    }

    @Override
    public void confirmEmail(UUID token) {
        var verificationToken = getVerificationToken(token, VerificationTokenType.CONFIRM_EMAIL);

        var user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        log.info("Enabled user {}!", user.getUsername());

        verificationTokenRepository.delete(verificationToken);
        log.info("Deleted email confirmation token for user {}!", user.getUsername());
    }

    @Override
    public void sendConfirmEmail(UserEmailDTO emailDTO) {
        var user = userRepository.findByEmail(emailDTO.getEmail()).orElse(null);
        if (user == null) {
            // if user is not found, fail silently
            return;
        }
        this.generateEmailConfirmationToken(user);
    }

    @Override
    public void resetPassword(UUID token, UserNewPasswordDTO newPasswordDTO) {
        var verificationToken = getVerificationToken(token, VerificationTokenType.RESET_PASSWORD);

        var user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPasswordDTO.getPassword()));
        userRepository.save(user);
        log.info("Changed password for user {}!", user.getUsername());

        verificationTokenRepository.delete(verificationToken);
        log.info("Deleted reset password token for user {}!", user.getUsername());
    }

    @Override
    public void sendResetPassword(UserEmailDTO emailDTO) {
        var user = userRepository.findByEmail(emailDTO.getEmail()).orElse(null);
        if (user == null) {
            // if user is not found, fail silently
            return;
        }
        this.generatePasswordResetToken(user);
    }

    private VerificationToken getVerificationToken(UUID token, VerificationTokenType expectedType) {
        var verificationToken = verificationTokenRepository.findById(token)
                .orElseThrow(() -> new ResourceNotFoundException("verificationTokenNotFound"));
        if (verificationToken.getExpiryDate().before(new Date())) {
            throw new ExpiredVerificationTokenException();
        }
        if (verificationToken.getType() != expectedType) {
            throw new InvalidTokenTypeException();
        }
        return verificationToken;
    }

    private VerificationToken generateToken(User user, VerificationTokenType type) {
        var tokenExpiration = new Date(new Date().getTime() + verificationTokenExpirationTimeMs);
        var token = new VerificationToken(null, user, tokenExpiration, type);
        verificationTokenRepository.save(token);

        return token;
    }

    private void generateEmailConfirmationToken(User user) {
        var token = generateToken(user, VerificationTokenType.CONFIRM_EMAIL);

        var context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("confirmationUrl", confirmEmailUrl + token.getId());

        mailService.sendEmail(user.getEmail(), "confirmEmail.subject", "confirmEmail", context);
    }

    private void generatePasswordResetToken(User user) {
        var token = generateToken(user, VerificationTokenType.RESET_PASSWORD);

        var context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("resetUrl", resetPasswordUrl + token.getId());

        mailService.sendEmail(user.getEmail(), "resetPassword.subject", "resetPassword", context);
    }
}
