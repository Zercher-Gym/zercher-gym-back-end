package zercher.be.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.CustomWorkout;
import zercher.be.model.entity.CustomWorkoutExercise;

import java.util.List;

public interface CustomWorkoutExerciseRepository extends JpaRepository<CustomWorkoutExercise, Long> {
    List<CustomWorkoutExercise> findByCustomWorkout(CustomWorkout customWorkout);

    void deleteCustomWorkoutExercisesByCustomWorkout(CustomWorkout customWorkout);
}
