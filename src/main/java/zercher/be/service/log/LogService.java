package zercher.be.service.log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zercher.be.dto.logs.WorkoutLogCreateDTO;
import zercher.be.dto.logs.WorkoutLogViewListDTO;

import java.util.UUID;

public interface LogService {
    void createWorkoutLog(WorkoutLogCreateDTO createDTO);

    Page<WorkoutLogViewListDTO> getWorkoutLogList(Pageable pageable);

    Page<WorkoutLogViewListDTO> getWorkoutLogListAdmin(UUID userId, Pageable pageable);

    void deleteWorkoutLog(UUID id);

    boolean workoutLogBelongsToUser(UUID id);
}
