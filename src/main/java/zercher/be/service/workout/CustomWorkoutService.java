package zercher.be.service.workout;

import zercher.be.dto.custom.CustomWorkoutCreateUpdateDTO;
import zercher.be.dto.custom.CustomWorkoutViewDTO;
import zercher.be.dto.custom.CustomWorkoutViewListDTO;

import java.util.List;
import java.util.UUID;

public interface CustomWorkoutService {
    CustomWorkoutViewDTO getCustomWorkout(UUID id);

    List<CustomWorkoutViewListDTO> getCustomWorkouts();

    List<CustomWorkoutViewListDTO> getCustomWorkoutsByUserId(UUID userId);

    void createCustomWorkout(CustomWorkoutCreateUpdateDTO createDTO);

    void updateCustomWorkout(UUID id, CustomWorkoutCreateUpdateDTO updateDTO);

    void deleteCustomWorkout(UUID id);

    Boolean customWorkoutBelongsToUser(UUID id);
}
