package zercher.be.repository.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zercher.be.model.entity.CustomExercise;
import zercher.be.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface CustomExerciseRepository extends JpaRepository<CustomExercise, UUID>, JpaSpecificationExecutor<CustomExercise> {
    Long countCustomExerciseByUser(User user);

    List<CustomExercise> findByUser(User user);
}
