package zercher.be.repository.customexercise;

import java.util.UUID;

public interface CustomExerciseQueryRepository {
    Integer getMaxExerciseLimit(UUID userId);

    Long getCustomExerciseCount(UUID userId);
}
