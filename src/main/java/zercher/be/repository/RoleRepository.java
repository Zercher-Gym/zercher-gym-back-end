package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import zercher.be.model.Role;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Set<Role> findAllByNameIn(Collection<String> names);
}
