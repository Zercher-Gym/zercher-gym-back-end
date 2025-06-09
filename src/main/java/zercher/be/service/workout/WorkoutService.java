package zercher.be.service.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zercher.be.dto.workout.*;
import zercher.be.dto.workout.WorkoutLabelUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface WorkoutService {
    WorkoutViewDTO getWorkout(UUID id);

    void createWorkout(WorkoutCreateDTO createDTO);

    void updateWorkout(UUID id, WorkoutUpdateDTO updateDTO);

    void updateWorkoutLabel(Long id, WorkoutLabelUpdateDTO updateDTO);

    Page<WorkoutViewListDTO> searchWorkoutsAdmin(Pageable pageable, WorkoutSearchAdminDTO searchAdminDTO);

    List<WorkoutViewListDTO> searchWorkouts(WorkoutSearchDTO searchDTO);

    void deleteWorkout(UUID id);
}
