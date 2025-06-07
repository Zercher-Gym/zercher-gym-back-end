package zercher.be.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.CustomWorkout;
import zercher.be.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface CustomWorkoutRepository extends JpaRepository<CustomWorkout, UUID> {
    Long countCustomWorkoutByUser(User user);

    List<CustomWorkout> findByUser(User user);
}

