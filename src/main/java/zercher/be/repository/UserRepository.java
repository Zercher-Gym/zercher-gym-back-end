package zercher.be.repository;

import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import zercher.be.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(@Email String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> getUsersByUsernameContainingIgnoreCase(String username, Limit limit);
}