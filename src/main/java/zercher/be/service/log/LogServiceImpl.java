package zercher.be.service.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zercher.be.dto.logs.ExerciseLogCreateDTO;
import zercher.be.dto.logs.WorkoutLogCreateDTO;
import zercher.be.dto.logs.WorkoutLogViewListDTO;
import zercher.be.dto.workout.WorkoutLabelViewDTO;
import zercher.be.exception.global.InvalidBusinessLogic;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.WorkoutLabelMapper;
import zercher.be.model.entity.ExerciseLog;
import zercher.be.model.entity.WorkoutLog;
import zercher.be.model.enums.Language;
import zercher.be.repository.UserRepository;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseLogRepository;
import zercher.be.repository.workout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final CustomWorkoutRepository customWorkoutRepository;
    private final CustomWorkoutExerciseRepository customWorkoutExerciseRepository;
    private final ExerciseLabelRepository exerciseLabelRepository;
    private final WorkoutLabelRepository workoutLabelRepository;

    private final WorkoutLabelMapper workoutLabelMapper;

    @Override
    public void createWorkoutLog(WorkoutLogCreateDTO createDTO) {
        if ((createDTO.getCustomWorkoutId() != null && createDTO.getWorkoutId() != null)
                || (createDTO.getCustomWorkoutId() == null && createDTO.getWorkoutId() == null)) {
            throw new InvalidBusinessLogic("onlyOneWorkoutTypeMustBeSpecified");
        }

        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var workoutLog = new WorkoutLog();
        workoutLog.setUser(user);

        if (createDTO.getWorkoutId() != null) {
            var workout = workoutRepository.findById(createDTO.getWorkoutId())
                    .orElseThrow(() -> new ResourceNotFoundException("workoutWithIdNotFound"));
            workoutLog.setWorkout(workout);
        }

        if (createDTO.getCustomWorkoutId() != null) {
            var customWorkout = customWorkoutRepository.findById(createDTO.getCustomWorkoutId())
                    .orElseThrow(() -> new ResourceNotFoundException("customWorkoutWithIdNotFound"));
            workoutLog.setCustomWorkout(customWorkout);
        }

        workoutLog.setDetails(createDTO.getDetails());
        workoutLogRepository.save(workoutLog);

        if (createDTO.getExercises() != null) {
            createExerciseLogs(workoutLog, createDTO.getExercises());
        }

        if (createDTO.getCustomExercises() != null) {
            createCustomExerciseLogs(workoutLog, createDTO.getCustomExercises());
        }
    }

    private void createExerciseLogs(WorkoutLog workoutLog, List<ExerciseLogCreateDTO> exercises) {
        List<ExerciseLog> exerciseLogs = new ArrayList<>();
        for (var exercise : exercises) {
            var workoutExerciseOptional = workoutExerciseRepository.findById(exercise.getWorkoutExerciseId());
            var customWorkoutExerciseOptional = customWorkoutExerciseRepository.findById(exercise.getWorkoutExerciseId());

            if (workoutExerciseOptional.isEmpty() && customWorkoutExerciseOptional.isEmpty()) {
                throw new InvalidBusinessLogic("workoutExerciseWithIdNotFound");
            }

            if (workoutExerciseOptional.isPresent()) {
                for (var details : exercise.getDetailsList()) {
                    var exerciseLog = new ExerciseLog();
                    exerciseLog.setWorkoutLog(workoutLog);
                    exerciseLog.setWorkoutExercise(workoutExerciseOptional.get());
                    exerciseLog.setDetails(details);
                    exerciseLog.setUser(workoutLog.getUser());
                    exerciseLogs.add(exerciseLog);
                }
            } else {
                for (var details : exercise.getDetailsList()) {
                    var exerciseLog = new ExerciseLog();
                    exerciseLog.setWorkoutLog(workoutLog);
                    exerciseLog.setCustomWorkoutExercise(customWorkoutExerciseOptional.get());
                    exerciseLog.setDetails(details);
                    exerciseLog.setUser(workoutLog.getUser());
                    exerciseLogs.add(exerciseLog);
                }
            }
        }
        exerciseLogRepository.saveAll(exerciseLogs);
    }

    private void createCustomExerciseLogs(WorkoutLog workoutLog, List<ExerciseLogCreateDTO> customExercises) {
        List<ExerciseLog> exerciseLogs = new ArrayList<>();
        for (var exercise : customExercises) {
            var customWorkoutExercise = customWorkoutExerciseRepository.findById(exercise.getWorkoutExerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("customWorkoutExerciseWithIdNotFound"));

            for (var details : exercise.getDetailsList()) {
                var exerciseLog = new ExerciseLog();
                exerciseLog.setWorkoutLog(workoutLog);
                exerciseLog.setCustomWorkoutExercise(customWorkoutExercise);
                exerciseLog.setUser(workoutLog.getUser());
                exerciseLog.setDetails(details);
                exerciseLogs.add(exerciseLog);
            }
        }
        exerciseLogRepository.saveAll(exerciseLogs);
    }

    @Override
    public Page<WorkoutLogViewListDTO> getWorkoutLogList(Pageable pageable) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var workoutLogs = workoutLogRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return workoutLogs.map(this::createWorkoutLogViewListDTO);
    }

    private WorkoutLogViewListDTO createWorkoutLogViewListDTO(WorkoutLog workoutLog) {
        var result = new WorkoutLogViewListDTO();
        result.setId(workoutLog.getId());
        result.setCreatedAt(workoutLog.getCreatedAt());

        if (workoutLog.getCustomWorkout() != null) {
            result.setTitle(workoutLog.getCustomWorkout().getTitle());
            result.setDescription(workoutLog.getCustomWorkout().getDescription());
        } else {
            var labels = new HashMap<Language, WorkoutLabelViewDTO>();

            var workoutLabels = workoutLabelRepository.findByWorkout(workoutLog.getWorkout());
            for (var workoutLabel : workoutLabels) {
                labels.put(workoutLabel.getLanguage(), workoutLabelMapper.entityToViewDTO(workoutLabel));
            }

            result.setLabels(labels);
        }

        return result;
    }

    @Override
    public void deleteWorkoutLog(UUID id) {
        var workoutLog = workoutLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutLogWithIdNotFound"));
        exerciseLogRepository.deleteExerciseLogsByWorkoutLog(workoutLog);
        workoutLogRepository.delete(workoutLog);
    }
}
