package zercher.be.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zercher.be.dto.customexercise.CustomExerciseCreateDTO;
import zercher.be.dto.customexercise.CustomExerciseUpdateDTO;
import zercher.be.dto.customexercise.CustomExerciseViewDTO;
import zercher.be.response.BaseResponse;
import zercher.be.service.exercise.CustomExerciseService;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Custom exercise")
@RequiredArgsConstructor
@RequestMapping("/api/exercise/custom")
@SecurityRequirement(name = "Authentication")
public class CustomExerciseController {
    private final CustomExerciseService customExerciseService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Void>> createCustomExercise(@Valid @RequestBody CustomExerciseCreateDTO createDTO) {
        customExerciseService.createCustomExercise(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateCustomExercise(@PathVariable UUID id, @Valid @RequestBody CustomExerciseUpdateDTO updateDTO) {
        if (!customExerciseService.customExerciseBelongsToUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>("customExerciseDoesNotBelongToUser"));
        }
        customExerciseService.updateCustomExercise(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<CustomExerciseViewDTO>>> getCustomExercises() {
        var customExercises = customExerciseService.getCustomExercises();
        return new ResponseEntity<>(new BaseResponse<>(customExercises), HttpStatus.OK);
    }

    @GetMapping("/admin/{userId}")
    public ResponseEntity<BaseResponse<List<CustomExerciseViewDTO>>> getCustomExercisesByAdmin(@PathVariable UUID userId) {
        var customExercises = customExerciseService.getCustomExercisesByUserId(userId);
        return new ResponseEntity<>(new BaseResponse<>(customExercises), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteCustomExercise(@PathVariable UUID id) {
        if (!customExerciseService.customExerciseBelongsToUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>("customExerciseDoesNotBelongToUser"));
        }
        customExerciseService.deleteCustomExercise(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @DeleteMapping("/custom/admin/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteCustomExerciseAdmin(@PathVariable UUID id) {
        customExerciseService.deleteCustomExercise(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
