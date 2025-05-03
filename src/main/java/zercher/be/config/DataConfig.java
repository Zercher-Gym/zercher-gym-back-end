package zercher.be.config;

import lombok.AllArgsConstructor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import zercher.be.model.Role;
import zercher.be.repository.RoleRepository;
import zercher.be.security.Authority;

@Component
@AllArgsConstructor
public class DataConfig implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (var authority : Authority.values()) {
            if (!roleRepository.existsByName(authority.getName())) {
                roleRepository.save(new Role(null, authority.getName()));
            }
        }
    }
}