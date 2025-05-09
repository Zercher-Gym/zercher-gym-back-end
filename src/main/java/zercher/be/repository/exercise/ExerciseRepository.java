package zercher.be.repository.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zercher.be.model.entity.Exercise;

import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID>, JpaSpecificationExecutor<Exercise> {
    boolean existsByIdentifier(String identifier);
}
