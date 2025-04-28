package zercher.service.authentication;

import zercher.dto.user.UserSignInDTO;
import zercher.dto.user.UserSignUpDTO;
import zercher.exception.ResourceExistsException;
import zercher.mapper.UserMapper;
import zercher.model.Role;
import zercher.model.User;
import zercher.repository.RoleRepository;
import zercher.repository.UserRepository;
import zercher.security.token.TokenUtilities;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final TokenUtilities tokenUtilities;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            TokenUtilities tokenUtilities,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.tokenUtilities = tokenUtilities;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String signIn(UserSignInDTO userSignInDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userSignInDTO.getUsername(), userSignInDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenUtilities.generateToken(authentication);
    }

    @Override
    public String signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.existsByUsername(userSignUpDTO.getUsername())) {
            throw new ResourceExistsException("User with username already exists.");
        }

        if (userRepository.existsByEmail(userSignUpDTO.getEmail())) {
            throw new ResourceExistsException("User with email already exists.");
        }

        var userRole = roleRepository
                .findByName("USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        var roles = new HashSet<Role>() {{
            add(userRole);
        }};

        var user = User.builder()
                .username(userSignUpDTO.getUsername())
                .email(userSignUpDTO.getEmail())
                .password(passwordEncoder.encode(userSignUpDTO.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        return signIn(userMapper.userSignUpDTOToUserSignInDTO(userSignUpDTO));
    }
}
