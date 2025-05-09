package zercher.be.service.exercise;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zercher.be.dto.exercise.*;
import zercher.be.dto.exerciselabel.ExerciseLabelUpdateDTO;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.ExerciseLabelMapper;
import zercher.be.mapper.ExerciseMapper;
import zercher.be.model.entity.ExerciseLabel;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseQueryRepository;
import zercher.be.repository.exercise.ExerciseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseQueryRepository exerciseQueryRepository;
    private final ExerciseLabelRepository exerciseLabelRepository;

    private final ExerciseMapper exerciseMapper;
    private final ExerciseLabelMapper exerciseLabelMapper;

    @Override
    public ExerciseViewAdminDTO getExercise(UUID id) {
        return exerciseQueryRepository.getView(id);
    }

    @Override
    public void createExercise(ExerciseCreateDTO createDTO) {
        if (exerciseRepository.existsByIdentifier(createDTO.getIdentifier())) {
            throw new ResourceExistsException("exerciseWithIdentifierExists");
        }

        var exercise = exerciseMapper.createDTOToExercise(createDTO);
        exerciseRepository.save(exercise);

        var exerciseLabels = new ArrayList<ExerciseLabel>();
        for (var labelDTO : createDTO.getLabels()) {
            var exerciseLabel = exerciseLabelMapper.createDTOToExerciseLabel(labelDTO);
            exerciseLabel.setExercise(exercise);
            exerciseLabels.add(exerciseLabel);
        }
        exerciseLabelRepository.saveAll(exerciseLabels);
    }

    @Override
    public void updateExerciseLabel(Long id, ExerciseLabelUpdateDTO updateDTO) {
        var exerciseLabel = exerciseLabelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exerciseLabelWithIdDoesNotExist"));
        exerciseLabelMapper.updateUserFromDTO(updateDTO, exerciseLabel);
        exerciseLabelRepository.save(exerciseLabel);
    }

    @Override
    public Page<ExerciseViewAdminDTO> searchExercisesAdmin(Pageable pageable, ExerciseSearchAdminDTO searchAdminDTO) {
        return exerciseQueryRepository.getSearchAdmin(pageable, searchAdminDTO);
    }

    @Override
    public List<ExerciseViewDTO> searchExercises(ExerciseSearchDTO searchDTO) {
        return exerciseQueryRepository.getSearch(searchDTO);
    }

    @Override
    @Transactional
    public void deleteExercise(UUID id) {
        var exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
        exerciseLabelRepository.deleteExerciseLabelsByExercise(exercise);
        exerciseRepository.delete(exercise);
    }
}
