package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import zercher.be.model.User;
import zercher.be.model.VerificationToken;

import java.util.List;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    List<VerificationToken> getByUser(User user);
}
