package zercher.be.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zercher.be.model.entity.Workout;

import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID>, JpaSpecificationExecutor<Workout> {
    boolean existsByIdentifier(String identifier);
}
