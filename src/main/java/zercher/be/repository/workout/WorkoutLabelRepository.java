package zercher.be.repository.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zercher.be.model.entity.Workout;
import zercher.be.model.entity.WorkoutLabel;

import java.util.List;

public interface WorkoutLabelRepository extends JpaRepository<WorkoutLabel, Long>, JpaSpecificationExecutor<WorkoutLabel> {
    void deleteWorkoutLabelsByWorkout(Workout workout);

    List<WorkoutLabel> findByWorkout(Workout workout);
}
