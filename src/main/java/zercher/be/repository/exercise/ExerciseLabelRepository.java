package zercher.be.repository.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zercher.be.model.entity.Exercise;
import zercher.be.model.entity.ExerciseLabel;

import java.util.List;

public interface ExerciseLabelRepository extends JpaRepository<ExerciseLabel, Long>, JpaSpecificationExecutor<ExerciseLabel> {
    void deleteExerciseLabelsByExercise(Exercise exercise);

    List<ExerciseLabel> findByExercise(Exercise exercise);
}
