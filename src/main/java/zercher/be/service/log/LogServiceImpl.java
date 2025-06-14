package zercher.be.service.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zercher.be.dto.logs.*;
import zercher.be.dto.workout.WorkoutLabelViewDTO;
import zercher.be.exception.global.InvalidBusinessLogic;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.WorkoutLabelMapper;
import zercher.be.model.entity.ExerciseLog;
import zercher.be.model.entity.WorkoutLog;
import zercher.be.model.enums.Language;
import zercher.be.repository.UserRepository;
import zercher.be.repository.exercise.ExerciseLogRepository;
import zercher.be.repository.workout.*;

import java.util.*;

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
        workoutLog.setDetails(createDTO.getDetails());

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
    }

    private void createExerciseLogs(WorkoutLog workoutLog, List<ExerciseLogCreateDTO> exercises) {
        List<ExerciseLog> exerciseLogs = new ArrayList<>();
        for (var exercise : exercises) {
            if (exercise.getWorkoutExerciseId() != null) {
                var workoutExercise = workoutExerciseRepository.findById(exercise.getWorkoutExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("workoutExerciseWithIdNotFound"));
                for (var details : exercise.getDetailsList()) {
                    var exerciseLog = new ExerciseLog();
                    exerciseLog.setWorkoutLog(workoutLog);
                    exerciseLog.setWorkoutExercise(workoutExercise);
                    exerciseLog.setDetails(details);
                    exerciseLog.setUser(workoutLog.getUser());
                    exerciseLogs.add(exerciseLog);
                }
            } else {
                var customWorkoutExercise = customWorkoutExerciseRepository.findById(exercise.getCustomWorkoutExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("customWorkoutExerciseWithIdNotFound"));
                for (var details : exercise.getDetailsList()) {
                    var exerciseLog = new ExerciseLog();
                    exerciseLog.setWorkoutLog(workoutLog);
                    exerciseLog.setCustomWorkoutExercise(customWorkoutExercise);
                    exerciseLog.setDetails(details);
                    exerciseLog.setUser(workoutLog.getUser());
                    exerciseLogs.add(exerciseLog);
                }
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

    @Override
    public Page<WorkoutLogViewListDTO> getWorkoutLogListAdmin(UUID userId, Pageable pageable) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));

        var workoutLogs = workoutLogRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return workoutLogs.map(this::createWorkoutLogViewListDTO);
    }

    @Override
    public WorkoutLogViewDTO getWorkoutLogById(UUID id) {
        var workoutLog = workoutLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutLogWithIdNotFound"));

        return createWorkoutLogViewDTO(workoutLog);
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

    private WorkoutLogViewDTO createWorkoutLogViewDTO(WorkoutLog workoutLog) {
        var exerciseLogs = exerciseLogRepository.findByWorkoutLog(workoutLog);

        var workoutLogViewDTO = new WorkoutLogViewDTO();
        workoutLogViewDTO.setId(workoutLog.getId());
        workoutLogViewDTO.setCreatedAt(workoutLog.getCreatedAt());
        workoutLogViewDTO.setDetails(workoutLog.getDetails());

        if (workoutLog.getCustomWorkout() != null) {
            workoutLogViewDTO.setTitle(workoutLog.getCustomWorkout().getTitle());
            workoutLogViewDTO.setDescription(workoutLog.getCustomWorkout().getDescription());
            workoutLogViewDTO.setCustomWorkoutId(workoutLog.getCustomWorkout().getId());
        } else {
            workoutLogViewDTO.setWorkoutId(workoutLog.getWorkout().getId());
            var labels = new HashMap<Language, WorkoutLabelViewDTO>();

            var workoutLabels = workoutLabelRepository.findByWorkout(workoutLog.getWorkout());
            for (var workoutLabel : workoutLabels) {
                labels.put(workoutLabel.getLanguage(), workoutLabelMapper.entityToViewDTO(workoutLabel));
            }

            workoutLogViewDTO.setLabels(labels);
        }

        workoutLogViewDTO.setExerciseLogs(new ArrayList<>());

        for (var exerciseLog : exerciseLogs) {
            var exerciseLogViewDTO = new ExerciseLogViewDTO();
            exerciseLogViewDTO.setDetails(exerciseLog.getDetails());
            if (exerciseLog.getWorkoutExercise() != null) {
                exerciseLogViewDTO.setWorkoutExerciseId(exerciseLog.getWorkoutExercise().getId());
            }
            if (exerciseLog.getCustomWorkoutExercise() != null) {
                exerciseLogViewDTO.setCustomWorkoutExerciseId(exerciseLog.getCustomWorkoutExercise().getId());
            }
            workoutLogViewDTO.getExerciseLogs().add(exerciseLogViewDTO);
        }

        return workoutLogViewDTO;
    }

    @Override
    public void deleteWorkoutLog(UUID id) {
        var workoutLog = workoutLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutLogWithIdNotFound"));
        exerciseLogRepository.deleteExerciseLogsByWorkoutLog(workoutLog);
        workoutLogRepository.delete(workoutLog);
    }

    public boolean workoutLogBelongsToUser(UUID id) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var workoutLog = workoutLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("workoutLogWithIdNotFound"));
        return workoutLog.getUser().equals(user);
    }
}
