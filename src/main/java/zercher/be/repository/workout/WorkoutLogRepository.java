package zercher.be.repository.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.User;
import zercher.be.model.entity.WorkoutLog;

import java.util.UUID;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {
    Page<WorkoutLog> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
