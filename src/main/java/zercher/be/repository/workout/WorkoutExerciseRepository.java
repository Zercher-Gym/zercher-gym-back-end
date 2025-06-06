package zercher.be.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.Workout;
import zercher.be.model.entity.WorkoutExercise;

import java.util.List;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    List<WorkoutExercise> findByWorkout(Workout workout);

    void deleteWorkoutExercisesByWorkout(Workout workout);
}
