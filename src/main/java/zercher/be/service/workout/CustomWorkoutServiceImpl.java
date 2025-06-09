package zercher.be.service.workout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zercher.be.dto.custom.CustomWorkoutCustomExerciseViewDTO;
import zercher.be.dto.custom.CustomWorkoutCreateUpdateDTO;
import zercher.be.dto.custom.CustomWorkoutViewDTO;
import zercher.be.dto.custom.CustomWorkoutViewListDTO;
import zercher.be.dto.exercise.CustomWorkoutExerciseViewDTO;
import zercher.be.dto.exercise.ExerciseLabelViewDTO;
import zercher.be.exception.global.InvalidBusinessLogic;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.exception.global.RoleLimitExceeded;
import zercher.be.mapper.*;
import zercher.be.model.entity.CustomWorkout;
import zercher.be.model.entity.CustomWorkoutExercise;
import zercher.be.model.enums.Language;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UnitRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.exercise.CustomExerciseRepository;
import zercher.be.repository.exercise.ExerciseLabelRepository;
import zercher.be.repository.exercise.ExerciseRepository;
import zercher.be.repository.workout.CustomWorkoutExerciseRepository;
import zercher.be.repository.workout.CustomWorkoutRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomWorkoutServiceImpl implements CustomWorkoutService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UnitRepository unitRepository;
    private final CustomWorkoutRepository customWorkoutRepository;
    private final CustomWorkoutExerciseRepository customWorkoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final CustomExerciseRepository customExerciseRepository;
    private final ExerciseLabelRepository exerciseLabelRepository;

    private final UnitMapper unitMapper;
    private final CustomWorkoutMapper customWorkoutMapper;
    private final CustomWorkoutExerciseMapper customWorkoutExerciseMapper;
    private final ExerciseLabelMapper exerciseLabelMapper;

    @Override
    public CustomWorkoutViewDTO getCustomWorkout(UUID id) {
        var customWorkout = customWorkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customWorkoutWithIdNotFound"));

        return customWorkoutToViewDTO(customWorkout);
    }

    @Override
    public List<CustomWorkoutViewListDTO> getCustomWorkouts() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        return getCustomWorkoutsByUserId(user.getId());
    }

    @Override
    public List<CustomWorkoutViewListDTO> getCustomWorkoutsByUserId(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));
        return customWorkoutRepository.findByUser(user).stream().map(customWorkoutMapper::customWorkoutToViewListDTO).collect(Collectors.toList());
    }

    @Override
    public void createCustomWorkout(CustomWorkoutCreateUpdateDTO createDTO) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var userMaxWorkoutLimit = roleRepository.getMaxWorkoutLimit(user);
        var userWorkoutCount = customWorkoutRepository.countCustomWorkoutByUser(user);

        if (userMaxWorkoutLimit == null) {
            userMaxWorkoutLimit = 0;
        }

        if (userWorkoutCount + 1 > userMaxWorkoutLimit) {
            throw new RoleLimitExceeded("workoutLimitExceeded");
        }

        var customWorkout = customWorkoutMapper.createDTOToCustomWorkout(createDTO);
        customWorkout.setUser(user);
        customWorkoutRepository.save(customWorkout);

        addCustomWorkoutExercises(customWorkout, createDTO);
    }

    @Override
    public void updateCustomWorkout(UUID id, CustomWorkoutCreateUpdateDTO updateDTO) {
        var customWorkout = customWorkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customWorkoutWithIdNotFound"));

        customWorkoutExerciseRepository.deleteCustomWorkoutExercisesByCustomWorkout(customWorkout);
        addCustomWorkoutExercises(customWorkout, updateDTO);
        customWorkoutMapper.updateCustomWorkoutFromDTO(updateDTO, customWorkout);

        customWorkoutRepository.save(customWorkout);
    }

    @Override
    public void deleteCustomWorkout(UUID id) {
        var customWorkout = customWorkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customWorkoutWithIdNotFound"));
        customWorkoutExerciseRepository.deleteCustomWorkoutExercisesByCustomWorkout(customWorkout);
        customWorkoutRepository.delete(customWorkout);
    }

    @Override
    public Boolean customWorkoutBelongsToUser(UUID id) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var customWorkout = customWorkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customWorkoutWithIdNotFound"));

        return customWorkout.getUser().equals(user);
    }

    private void addCustomWorkoutExercises(CustomWorkout customWorkout, CustomWorkoutCreateUpdateDTO createDTO) {
        var customWorkoutExercises = new ArrayList<CustomWorkoutExercise>();

        for (var exerciseDTO : createDTO.getExercises()) {
            var unit = unitRepository.findById(exerciseDTO.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));

            var customWorkoutExercise = new CustomWorkoutExercise();
            customWorkoutExercise.setCustomWorkout(customWorkout);
            customWorkoutExercise.setUnit(unit);
            customWorkoutExercise.setQuantity(exerciseDTO.getQuantity());

            var exercise = exerciseRepository.findById(exerciseDTO.getExerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
            if (!exercise.getUnits().contains(unit)) {
                throw new InvalidBusinessLogic("exerciseDoesNotHaveProvidedUnit");
            }
            customWorkoutExercise.setExercise(exercise);
            customWorkoutExercise.setCustomExercise(null);

            customWorkoutExercises.add(customWorkoutExercise);
        }

        for (var customExerciseDTO : createDTO.getCustomExercises()) {
            var unit = unitRepository.findById(customExerciseDTO.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));

            var customWorkoutExercise = new CustomWorkoutExercise();
            customWorkoutExercise.setCustomWorkout(customWorkout);
            customWorkoutExercise.setUnit(unit);
            customWorkoutExercise.setQuantity(customExerciseDTO.getQuantity());

            var customExercise = customExerciseRepository.findById(customExerciseDTO.getCustomExerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("exerciseWithIdNotFound"));
            if (!customExercise.getUnit().equals(unit)) {
                throw new InvalidBusinessLogic("exerciseDoesNotHaveProvidedUnit");
            }
            customWorkoutExercise.setExercise(null);
            customWorkoutExercise.setCustomExercise(customExercise);

            customWorkoutExercises.add(customWorkoutExercise);
        }

        customWorkoutExerciseRepository.saveAll(customWorkoutExercises);
    }

    private CustomWorkoutViewDTO customWorkoutToViewDTO(CustomWorkout customWorkout) {
        var customWorkoutViewDTO = customWorkoutMapper.customWorkoutToViewDTO(customWorkout);

        var exercises = new HashSet<CustomWorkoutExerciseViewDTO>();
        var customExercises = new HashSet<CustomWorkoutCustomExerciseViewDTO>();

        var customWorkoutExercises = customWorkoutExerciseRepository.findByCustomWorkout(customWorkout);
        for (var customWorkoutExercise : customWorkoutExercises) {
            if (customWorkoutExercise.getCustomExercise() != null) {
                var customExercise = customWorkoutExerciseMapper.customWorkoutExerciseToCustomWorkoutExerciseViewDTO(customWorkoutExercise);
                customExercises.add(customExercise);
            } else {
                var exercise = customWorkoutExerciseMapper.customWorkoutExerciseToWorkoutExerciseViewDTO(customWorkoutExercise);

                var labels = new HashMap<Language, ExerciseLabelViewDTO>();
                var exerciseLabels = exerciseLabelRepository.findByExercise(customWorkoutExercise.getExercise());
                for (var exerciseLabel : exerciseLabels) {
                    labels.put(exerciseLabel.getLanguage(), exerciseLabelMapper.entityToViewDTO(exerciseLabel));
                }

                var units = customWorkoutExercise.getExercise().getUnits().stream().map(unitMapper::entityToViewDTO).collect(Collectors.toSet());

                exercise.setLabels(labels);
                exercise.setUnits(units);
                exercises.add(exercise);
            }
        }

        customWorkoutViewDTO.setExercises(exercises);
        customWorkoutViewDTO.setCustomExercises(customExercises);

        return customWorkoutViewDTO;
    }
}
