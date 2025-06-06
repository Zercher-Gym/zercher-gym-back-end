package zercher.be.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zercher.be.dto.workout.*;
import zercher.be.dto.workoutlabel.WorkoutLabelUpdateDTO;
import zercher.be.response.BaseResponse;
import zercher.be.response.PageResponse;
import zercher.be.service.workout.WorkoutService;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Workout")
@RequiredArgsConstructor
@RequestMapping("/api/workout")
@SecurityRequirement(name = "Authentication")
public class WorkoutController {
    private final WorkoutService workoutService;

    @Tag(name = "Admin")
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> createWorkout(@Valid @RequestBody WorkoutCreateDTO createDTO) {
        workoutService.createWorkout(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<WorkoutViewDTO>> getWorkout(@PathVariable UUID id) {
        var workoutView = workoutService.getWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(workoutView));
    }

    @Tag(name = "Admin")
    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<PageResponse<WorkoutViewListDTO>> searchWorkoutAdmin(@ParameterObject Pageable pageable, @Valid WorkoutSearchAdminDTO searchAdminDTO) {
        var page = workoutService.searchWorkoutsAdmin(pageable, searchAdminDTO);
        return new ResponseEntity<>(new PageResponse<>(page), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<WorkoutViewListDTO>>> searchWorkout(@Valid WorkoutSearchDTO searchDTO) {
        var list = workoutService.searchWorkouts(searchDTO);
        return new ResponseEntity<>(new BaseResponse<>(list), HttpStatus.OK);
    }

    @Tag(name = "Admin")
    @PutMapping("/label/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> updateLabel(@PathVariable Long id, @Valid @RequestBody WorkoutLabelUpdateDTO updateDTO) {
        workoutService.updateWorkoutLabel(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @Tag(name = "Admin")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> updateWorkout(@PathVariable UUID id, @Valid @RequestBody WorkoutUpdateDTO updateDTO) {
        workoutService.updateWorkout(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @Tag(name = "Admin")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> deleteWorkout(@PathVariable UUID id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
