package zercher.be.service.authentication;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import zercher.be.dto.user.UserSignInDTO;
import zercher.be.dto.user.UserSignUpDTO;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.model.User;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.security.Authority;
import zercher.be.security.token.TokenUtilities;

import java.util.Collections;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final TokenUtilities tokenUtilities;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String signIn(UserSignInDTO userSignInDTO) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userSignInDTO.getUsername(), userSignInDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = (UserDetails) authentication.getPrincipal();

        return tokenUtilities.generateToken(userDetails);
    }

    @Override
    public void signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.existsByUsername(userSignUpDTO.getUsername())) {
            throw new ResourceExistsException("userWithUsernameExists");
        }

        if (userRepository.existsByEmail(userSignUpDTO.getEmail())) {
            throw new ResourceExistsException("userWithEmailExists");
        }

        var role = roleRepository.findByName(Authority.USER.getName())
                .orElseThrow(() -> new ResourceNotFoundException("roleNotFound"));

        var user = User.builder()
                .username(userSignUpDTO.getUsername())
                .email(userSignUpDTO.getEmail())
                .password(passwordEncoder.encode(userSignUpDTO.getPassword()))
                .enabled(false)
                .roles(Collections.singleton(role))
                .build();

        userRepository.save(user);
    }
}
