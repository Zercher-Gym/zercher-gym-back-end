package zercher.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.JournalLog;

import java.util.UUID;

public interface JournalLogRepository extends JpaRepository<JournalLog, UUID> {
}
