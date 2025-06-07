package zercher.be.service.workout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zercher.be.dto.exerciselabel.ExerciseLabelViewDTO;
import zercher.be.dto.workout.*;
import zercher.be.dto.workout.WorkoutExerciseCreateUpdateDTO;
import zercher.be.dto.workoutlabel.WorkoutLabelUpdateDTO;
import zercher.be.exception.global.InvalidBusinessLogic;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.ExerciseLabelMapper;
import zercher.be.mapper.WorkoutExerciseMapper;
import zercher.be.mapper.WorkoutLabelMapper;
import zercher.be.mapper.WorkoutMapper;
import zercher.be.model.entity.Workout;
import zercher.be.model.entity.WorkoutExercise;
import zercher.be.model.entity.WorkoutLabel;
import zercher.be.model.enums.Language;
import zercher.be.repository.UnitRepository;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseRepository;
import zercher.be.repository.workout.WorkoutExerciseRepository;
import zercher.be.repository.workout.WorkoutLabelRepository;
import zercher.be.repository.workout.WorkoutRepository;
import zercher.be.specification.WorkoutSpecifications;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutLabelRepository workoutLabelRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseLabelRepository exerciseLabelRepository;
    private final ExerciseRepository exerciseRepository;
    private final UnitRepository unitRepository;

    private final WorkoutMapper workoutMapper;
    private final WorkoutLabelMapper workoutLabelMapper;
    private final WorkoutExerciseMapper workoutExerciseMapper;
    private final ExerciseLabelMapper exerciseLabelMapper;

    @Override
    public WorkoutViewDTO getWorkout(UUID id) {
        var workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutWithIdNotFound"));
        return workoutToViewDTO(workout);
    }

    @Override
    public void createWorkout(WorkoutCreateDTO createDTO) {
        if (workoutRepository.existsByIdentifier(createDTO.getIdentifier())) {
            throw new ResourceExistsException("workoutWithIdentifierExists");
        }

        var workout = workoutMapper.createDTOToWorkout(createDTO);

        workoutRepository.save(workout);

        addWorkoutExercises(workout, createDTO.getExercises());

        var workoutLabels = new ArrayList<WorkoutLabel>();
        for (var labelDTO : createDTO.getLabels()) {
            var workoutLabel = workoutLabelMapper.createDTOToWorkoutLabel(labelDTO);
            workoutLabel.setWorkout(workout);
            workoutLabels.add(workoutLabel);
        }

        workoutLabelRepository.saveAll(workoutLabels);
    }

    @Override
    public void updateWorkout(UUID id, WorkoutUpdateDTO updateDTO) {
        var workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutWithIdNotFound"));

        if (!workout.getIdentifier().equals(updateDTO.getIdentifier()) && workoutRepository.existsByIdentifier(updateDTO.getIdentifier())) {
            throw new ResourceExistsException("workoutWithIdentifierExists");
        }
        workout.setIdentifier(updateDTO.getIdentifier());

        workoutExerciseRepository.deleteWorkoutExercisesByWorkout(workout);
        addWorkoutExercises(workout, updateDTO.getExercises());

        workoutRepository.save(workout);
    }

    private void addWorkoutExercises(Workout workout, Set<WorkoutExerciseCreateUpdateDTO> exercises) {
        var workoutExercises = new ArrayList<WorkoutExercise>();
        for (var exerciseData : exercises) {
            var exercise = exerciseRepository.findById(exerciseData.getExerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
            var unit = unitRepository.findById(exerciseData.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));
            if (!exercise.getUnits().contains(unit)) {
                throw new InvalidBusinessLogic("exerciseDoesNotHaveProvidedUnit");
            }
            var workoutExercise = new WorkoutExercise();
            workoutExercise.setWorkout(workout);
            workoutExercise.setExercise(exercise);
            workoutExercise.setUnit(unit);
            workoutExercise.setQuantity(exerciseData.getQuantity());
            workoutExercises.add(workoutExercise);
        }
        workoutExerciseRepository.saveAll(workoutExercises);
    }

    @Override
    public void updateWorkoutLabel(Long id, WorkoutLabelUpdateDTO updateDTO) {
        var workoutLabel = workoutLabelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutLabelWithIdDoesNotExist"));
        workoutLabelMapper.updateUserFromDTO(updateDTO, workoutLabel);
        workoutLabelRepository.save(workoutLabel);
    }

    @Override
    public Page<WorkoutViewListDTO> searchWorkoutsAdmin(Pageable pageable, WorkoutSearchAdminDTO searchAdminDTO) {
        Specification<Workout> specification = Specification.where(null);

        if (searchAdminDTO.getIdentifier() != null && !searchAdminDTO.getIdentifier().isEmpty()) {
            specification = specification.and(WorkoutSpecifications.identifierContains(searchAdminDTO.getIdentifier()));
        }
        if (searchAdminDTO.getTitle() != null && !searchAdminDTO.getTitle().isEmpty()) {
            specification = specification.and(WorkoutSpecifications.titleContains(searchAdminDTO.getTitle()));
        }
        if (searchAdminDTO.getDescription() != null && !searchAdminDTO.getDescription().isEmpty()) {
            specification = specification.and(WorkoutSpecifications.descriptionContains(searchAdminDTO.getDescription()));
        }
        if (searchAdminDTO.getLanguage() != null) {
            specification = specification.and(WorkoutSpecifications.languageIs(searchAdminDTO.getLanguage()));
        }

        var workouts = workoutRepository.findAll(specification, pageable);

        var workoutResults = new ArrayList<WorkoutViewListDTO>();
        for (var workout : workouts) {
            workoutResults.add(workoutToViewListDTO(workout));
        }
        return new PageImpl<>(workoutResults, pageable, workouts.getTotalElements());
    }

    @Override
    public List<WorkoutViewListDTO> searchWorkouts(WorkoutSearchDTO searchDTO) {
        Specification<Workout> specification = Specification.where(null);

        if (searchDTO.getContains() != null && !searchDTO.getContains().isEmpty()) {
            specification = specification.and(WorkoutSpecifications.contains(searchDTO.getContains()));
        }

        var workouts = workoutRepository.findAll(specification, PageRequest.of(0, searchDTO.getLimit()));

        var workoutResults = new ArrayList<WorkoutViewListDTO>();
        for (var workout : workouts) {
            workoutResults.add(workoutToViewListDTO(workout));
        }
        return workoutResults;
    }

    @Override
    @Transactional
    public void deleteWorkout(UUID id) {
        var workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutWithIdNotFound"));
        workoutLabelRepository.deleteWorkoutLabelsByWorkout(workout);
        workoutExerciseRepository.deleteWorkoutExercisesByWorkout(workout);
        workoutRepository.delete(workout);
    }

    private WorkoutViewListDTO workoutToViewListDTO(Workout workout) {
        var workoutResult = workoutMapper.entityToViewListDTO(workout);

        workoutResult.setLabels(new HashMap<>());
        var workoutLabels = workoutLabelRepository.findByWorkout(workout);
        for (var workoutLabel : workoutLabels) {
            workoutResult.getLabels().put(workoutLabel.getLanguage(), workoutLabelMapper.entityToViewDTO(workoutLabel));
        }

        return workoutResult;
    }

    private WorkoutViewDTO workoutToViewDTO(Workout workout) {
        var workoutResult = workoutMapper.entityToViewDTO(workout);

        workoutResult.setLabels(new HashMap<>());
        var workoutLabels = workoutLabelRepository.findByWorkout(workout);
        for (var workoutLabel : workoutLabels) {
            workoutResult.getLabels().put(workoutLabel.getLanguage(), workoutLabelMapper.entityToViewDTO(workoutLabel));
        }

        workoutResult.setExercises(new HashSet<>());
        var workoutExercises = workoutExerciseRepository.findByWorkout(workout);
        for (var workoutExercise : workoutExercises) {
            var exerciseView = workoutExerciseMapper.entityToViewDTO(workoutExercise);

            var labels = new HashMap<Language, ExerciseLabelViewDTO>();
            var exerciseLabels = exerciseLabelRepository.findByExercise(workoutExercise.getExercise());
            for (var exerciseLabel : exerciseLabels) {
                labels.put(exerciseLabel.getLanguage(), exerciseLabelMapper.entityToViewDTO(exerciseLabel));
            }

            exerciseView.setLabels(labels);
            workoutResult.getExercises().add(exerciseView);
        }

        return workoutResult;
    }
}
