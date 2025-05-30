package zercher.be.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import zercher.be.model.entity.User;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.model.enums.Role;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataConfig implements ApplicationRunner {
    @Value("${com.zercher.be.admin.username}")
    private String adminUsername;

    @Value("${com.zercher.be.admin.email}")
    private String adminEmail;

    @Value("${com.zercher.be.admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Set<zercher.be.model.entity.Role> unsavedRoles = new HashSet<>();
        for (var authority : Role.values()) {
            if (!roleRepository.existsByName(authority.getName())) {
                unsavedRoles.add(
                        new zercher.be.model.entity.Role(
                                null,
                                authority.getName(),
                                null
                        )
                );
            }
        }
        roleRepository.saveAll(unsavedRoles);

        var allRoles = new HashSet<>(roleRepository.findAll());
        if (!userRepository.existsByUsername(adminUsername) && !userRepository.existsByEmail(adminEmail)) {
            var user = User.builder()
                    .password(passwordEncoder.encode(adminPassword))
                    .username(adminUsername)
                    .email(adminEmail)
                    .enabled(true)
                    .locked(false)
                    .roles(allRoles)
                    .build();

            userRepository.save(user);
        }
    }
}