package zercher.be.service.exercise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import zercher.be.dto.custom.CustomExerciseCreateDTO;
import zercher.be.dto.custom.CustomExerciseUpdateDTO;
import zercher.be.dto.custom.CustomExerciseViewDTO;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.exception.global.RoleLimitExceeded;
import zercher.be.mapper.CustomExerciseMapper;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UnitRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.exercise.CustomExerciseRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomExerciseServiceImpl implements CustomExerciseService {
    private final CustomExerciseRepository customExerciseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final CustomExerciseMapper customExerciseMapper;
    private final UnitRepository unitRepository;

    @Override
    public List<CustomExerciseViewDTO> getCustomExercises() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        return getCustomExercisesByUserId(user.getId());
    }

    @Override
    public List<CustomExerciseViewDTO> getCustomExercisesByUserId(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));
        return customExerciseRepository.findByUser(user).stream().map(customExerciseMapper::customExerciseToViewDTO).collect(Collectors.toList());
    }

    @Override
    public void createCustomExercise(CustomExerciseCreateDTO createDTO) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var userMaxExerciseLimit = roleRepository.getMaxExerciseLimit(user);
        var userExerciseCount = customExerciseRepository.countCustomExerciseByUser(user);

        if(userMaxExerciseLimit == null) {
            userMaxExerciseLimit = 0;
        }

        if (userExerciseCount + 1 > userMaxExerciseLimit) {
            throw new RoleLimitExceeded("exerciseLimitExceeded");
        }

        var unit = unitRepository.findById(createDTO.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));

        var customExercise = customExerciseMapper.createDTOToCustomExercise(createDTO);
        customExercise.setUser(user);
        customExercise.setUnit(unit);
        customExerciseRepository.save(customExercise);
    }

    @Override
    public void updateCustomExercise(UUID id, CustomExerciseUpdateDTO updateDTO) {
        var customExercise = customExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customExerciseWithIdNotFound"));
        var unit = unitRepository.findById(updateDTO.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("unitWithIdNotFound"));
        customExerciseMapper.updateCustomExerciseFromDTO(updateDTO, customExercise);
        customExercise.setUnit(unit);
        customExerciseRepository.save(customExercise);
    }

    @Override
    public void deleteCustomExercise(UUID id) {
        var customExercise = customExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customExerciseWithIdNotFound"));
        customExerciseRepository.delete(customExercise);
    }

    @Override
    public Boolean customExerciseBelongsToUser(UUID id) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        var customExercise = customExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customExerciseWithIdNotFound"));

        return customExercise.getUser().equals(user);
    }
}
