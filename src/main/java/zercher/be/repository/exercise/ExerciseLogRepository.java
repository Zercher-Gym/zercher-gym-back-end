package zercher.be.repository.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import zercher.be.model.entity.ExerciseLog;
import zercher.be.model.entity.WorkoutLog;

import java.util.List;
import java.util.UUID;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, UUID> {
    void deleteExerciseLogsByWorkoutLog(WorkoutLog workoutLog);

    List<ExerciseLog> findByWorkoutLog(WorkoutLog workoutLog);
}
