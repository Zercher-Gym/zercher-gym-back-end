package zercher.be.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import zercher.be.model.entity.User;
import zercher.be.model.enums.ActivityType;
import zercher.be.repository.ActivityTypeRepository;
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
    private final ActivityTypeRepository activityTypeRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Set<zercher.be.model.entity.Role> unsavedRoles = new HashSet<>();
        for (var authority : Role.values()) {
            if (!roleRepository.existsByName(authority.getName())) {
                unsavedRoles.add(new zercher.be.model.entity.Role(null, authority.getName()));
            }
        }
        roleRepository.saveAll(unsavedRoles);

        var allRoles = new HashSet<>(roleRepository.findAll());
        if (!userRepository.existsByUsername(adminUsername)) {
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

        Set<zercher.be.model.entity.ActivityType> unsavedActivityTypes = new HashSet<>();
        for (var activityType : ActivityType.values()) {
            if (!activityTypeRepository.existsByName(activityType.getName())) {
                unsavedActivityTypes.add(new zercher.be.model.entity.ActivityType(null, activityType.getName()));
            }
        }
        activityTypeRepository.saveAll(unsavedActivityTypes);
    }
}