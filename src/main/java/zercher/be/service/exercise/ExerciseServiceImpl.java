package zercher.be.service.exercise;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zercher.be.dto.exercise.*;
import zercher.be.dto.exerciselabel.ExerciseLabelUpdateDTO;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.ExerciseLabelMapper;
import zercher.be.mapper.ExerciseMapper;
import zercher.be.mapper.UnitMapper;
import zercher.be.model.entity.Exercise;
import zercher.be.model.entity.ExerciseLabel;
import zercher.be.repository.UnitRepository;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseRepository;
import zercher.be.specification.ExerciseSpecifications;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseLabelRepository exerciseLabelRepository;
    private final UnitRepository unitRepository;

    private final ExerciseMapper exerciseMapper;
    private final ExerciseLabelMapper exerciseLabelMapper;
    private final UnitMapper unitMapper;

    @Override
    public ExerciseViewAdminDTO getExercise(UUID id) {
        var exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
        return exerciseToViewAdminDTO(exercise);
    }

    @Override
    public void createExercise(ExerciseCreateDTO createDTO) {
        if (exerciseRepository.existsByIdentifier(createDTO.getIdentifier())) {
            throw new ResourceExistsException("exerciseWithIdentifierExists");
        }

        var exercise = exerciseMapper.createDTOToExercise(createDTO);

        var units = unitRepository.findAllById(createDTO.getUnits());
        exercise.setUnits(new HashSet<>(units));

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
    public void updateExercise(UUID id, ExerciseUpdateDTO updateDTO) {
        var exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
        if (!exercise.getIdentifier().equals(updateDTO.getIdentifier()) && exerciseRepository.existsByIdentifier(updateDTO.getIdentifier())) {
            throw new ResourceExistsException("exerciseWithIdentifierExists");
        }

        exercise.setIdentifier(updateDTO.getIdentifier());
        var units = unitRepository.findAllById(updateDTO.getUnits());
        exercise.setUnits(new HashSet<>(units));
        exerciseRepository.save(exercise);
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
        Specification<Exercise> specification = Specification.where(null);

        if (searchAdminDTO.getIdentifier() != null && !searchAdminDTO.getIdentifier().isEmpty()) {
            specification = specification.and(ExerciseSpecifications.identifierContains(searchAdminDTO.getIdentifier()));
        }
        if (searchAdminDTO.getTitle() != null && !searchAdminDTO.getTitle().isEmpty()) {
            specification = specification.and(ExerciseSpecifications.titleContains(searchAdminDTO.getTitle()));
        }
        if (searchAdminDTO.getDescription() != null && !searchAdminDTO.getDescription().isEmpty()) {
            specification = specification.and(ExerciseSpecifications.descriptionContains(searchAdminDTO.getDescription()));
        }
        if (searchAdminDTO.getLanguage() != null) {
            specification = specification.and(ExerciseSpecifications.languageIs(searchAdminDTO.getLanguage()));
        }

        var exercises = exerciseRepository.findAll(specification, pageable);

        var exerciseResults = new ArrayList<ExerciseViewAdminDTO>();
        for (var exercise : exercises) {
            exerciseResults.add(exerciseToViewAdminDTO(exercise));
        }
        return new PageImpl<>(exerciseResults, pageable, exercises.getTotalElements());
    }

    @Override
    public List<ExerciseViewDTO> searchExercises(ExerciseSearchDTO searchDTO) {
        Specification<Exercise> specification = Specification.where(null);

        if (searchDTO.getContains() != null && !searchDTO.getContains().isEmpty()) {
            specification = specification.and(ExerciseSpecifications.contains(searchDTO.getContains()));
        }

        var exercises = exerciseRepository.findAll(specification, PageRequest.of(0, searchDTO.getLimit()));

        var exerciseResults = new ArrayList<ExerciseViewDTO>();
        for (var exercise : exercises) {
            exerciseResults.add(exerciseToViewDTO(exercise));
        }
        return exerciseResults;
    }

    @Override
    @Transactional
    public void deleteExercise(UUID id) {
        var exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
        exerciseLabelRepository.deleteExerciseLabelsByExercise(exercise);
        exerciseRepository.delete(exercise);
    }

    private ExerciseViewDTO exerciseToViewDTO(Exercise exercise) {
        var exerciseResult = exerciseMapper.entityToViewDTO(exercise);

        exerciseResult.setLabels(new HashMap<>());
        var exerciseLabels = exerciseLabelRepository.findByExercise(exercise);
        for (var exerciseLabel : exerciseLabels) {
            exerciseResult.getLabels().put(exerciseLabel.getLanguage(), exerciseLabelMapper.entityToViewDTO(exerciseLabel));
        }

        exerciseResult.setUnits(new HashSet<>());
        for (var exerciseUnit : exercise.getUnits()) {
            exerciseResult.getUnits().add(unitMapper.entityToViewDTO(exerciseUnit));
        }

        return exerciseResult;
    }

    private ExerciseViewAdminDTO exerciseToViewAdminDTO(Exercise exercise) {
        var exerciseResult = exerciseMapper.entityToViewAdminDTO(exercise);

        exerciseResult.setLabels(new HashSet<>());
        var exerciseLabels = exerciseLabelRepository.findByExercise(exercise);
        for (var exerciseLabel : exerciseLabels) {
            exerciseResult.getLabels().add(exerciseLabelMapper.entityToViewAdminDTO(exerciseLabel));
        }

        exerciseResult.setUnits(new HashSet<>());
        for (var exerciseUnit : exercise.getUnits()) {
            exerciseResult.getUnits().add(unitMapper.entityToViewDTO(exerciseUnit));
        }

        return exerciseResult;
    }
}
