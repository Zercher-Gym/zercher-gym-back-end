package zercher.be.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zercher.be.dto.customworkout.CustomWorkoutViewDTO;
import zercher.be.dto.customworkout.CustomWorkoutCreateUpdateDTO;
import zercher.be.dto.customworkout.CustomWorkoutViewListDTO;
import zercher.be.response.BaseResponse;
import zercher.be.service.workout.CustomWorkoutService;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Custom workout")
@RequiredArgsConstructor
@RequestMapping("/api/workout/custom")
@SecurityRequirement(name = "Authentication")
public class CustomWorkoutController {
    private final CustomWorkoutService customWorkoutService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Void>> createCustomWorkout(@Valid @RequestBody CustomWorkoutCreateUpdateDTO createDTO) {
        customWorkoutService.createCustomWorkout(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateCustomWorkout(@PathVariable UUID id, @Valid @RequestBody CustomWorkoutCreateUpdateDTO updateDTO) {
        if (!customWorkoutService.customWorkoutBelongsToUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>("customWorkoutDoesNotBelongToUser"));
        }
        customWorkoutService.updateCustomWorkout(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CustomWorkoutViewDTO>> getCustomWorkout(@PathVariable UUID id) {
        if (!customWorkoutService.customWorkoutBelongsToUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>("customWorkoutDoesNotBelongToUser"));
        }
        var customWorkout = customWorkoutService.getCustomWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(customWorkout));
    }

    @Tag(name = "Admin")
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<CustomWorkoutViewDTO>> getCustomWorkoutAdmin(@PathVariable UUID id) {
        var customWorkout = customWorkoutService.getCustomWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(customWorkout));
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<CustomWorkoutViewListDTO>>> getCustomWorkouts() {
        var customWorkouts = customWorkoutService.getCustomWorkouts();
        return new ResponseEntity<>(new BaseResponse<>(customWorkouts), HttpStatus.OK);
    }

    @Tag(name = "Admin")
    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<List<CustomWorkoutViewListDTO>>> getCustomWorkoutsByAdmin(@PathVariable UUID userId) {
        var customWorkouts = customWorkoutService.getCustomWorkoutsByUserId(userId);
        return new ResponseEntity<>(new BaseResponse<>(customWorkouts), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteCustomWorkout(@PathVariable UUID id) {
        if (!customWorkoutService.customWorkoutBelongsToUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>("customWorkoutDoesNotBelongToUser"));
        }
        customWorkoutService.deleteCustomWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @Tag(name = "Admin")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> deleteCustomWorkoutAdmin(@PathVariable UUID id) {
        customWorkoutService.deleteCustomWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
