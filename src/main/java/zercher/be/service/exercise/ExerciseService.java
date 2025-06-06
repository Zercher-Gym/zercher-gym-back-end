package zercher.be.service.exercise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zercher.be.dto.exercise.*;
import zercher.be.dto.exerciselabel.ExerciseLabelUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface ExerciseService {
    ExerciseViewAdminDTO getExercise(UUID id);

    void createExercise(ExerciseCreateDTO createDTO);

    void updateExercise(UUID id, ExerciseUpdateDTO updateDTO);

    void updateExerciseLabel(Long id, ExerciseLabelUpdateDTO updateDTO);

    Page<ExerciseViewAdminDTO> searchExercisesAdmin(Pageable pageable, ExerciseSearchAdminDTO searchAdminDTO);

    List<ExerciseViewDTO> searchExercises(ExerciseSearchDTO searchDTO);

    void deleteExercise(UUID id);
}
