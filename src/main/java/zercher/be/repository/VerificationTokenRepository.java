package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import zercher.be.model.entity.User;
import zercher.be.model.entity.VerificationToken;

import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    void deleteAllByUser(User user);
}
