package zercher.be.service.exercise;

import zercher.be.dto.customexercise.CustomExerciseCreateDTO;
import zercher.be.dto.customexercise.CustomExerciseUpdateDTO;
import zercher.be.dto.customexercise.CustomExerciseViewDTO;

import java.util.List;
import java.util.UUID;

public interface CustomExerciseService {
    List<CustomExerciseViewDTO> getCustomExercises();

    List<CustomExerciseViewDTO> getCustomExercisesByUserId(UUID userId);

    void createCustomExercise(CustomExerciseCreateDTO createDTO);

    void updateCustomExercise(UUID id, CustomExerciseUpdateDTO updateDTO);

    void deleteCustomExercise(UUID id);

    Boolean customExerciseBelongsToUser(UUID id);
}
