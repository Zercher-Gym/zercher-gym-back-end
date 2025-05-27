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
import zercher.be.dto.exercise.*;
import zercher.be.dto.exerciselabel.ExerciseLabelUpdateDTO;
import zercher.be.response.BaseResponse;
import zercher.be.response.PageResponse;
import zercher.be.service.exercise.ExerciseService;

import java.util.UUID;
import java.util.List;

@RestController
@Tag(name = "Exercise")
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
@SecurityRequirement(name = "Authentication")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @Tag(name = "Admin")
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> createExercise(@Valid @RequestBody ExerciseCreateDTO createDTO) {
        exerciseService.createExercise(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ExerciseViewAdminDTO>> getExercise(@PathVariable UUID id) {
        var exerciseView = exerciseService.getExercise(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(exerciseView));
    }

    @Tag(name = "Admin")
    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<PageResponse<ExerciseViewAdminDTO>> searchExerciseAdmin(@ParameterObject Pageable pageable, @Valid ExerciseSearchAdminDTO searchAdminDTO) {
        var page = exerciseService.searchExercisesAdmin(pageable, searchAdminDTO);
        return new ResponseEntity<>(new PageResponse<>(page), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<ExerciseViewDTO>>> searchExercise(@Valid ExerciseSearchDTO searchDTO) {
        var list = exerciseService.searchExercises(searchDTO);
        return new ResponseEntity<>(new BaseResponse<>(list), HttpStatus.OK);
    }

    @Tag(name = "Admin")
    @PutMapping("/label/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> updateLabel(@PathVariable Long id, @Valid @RequestBody ExerciseLabelUpdateDTO updateDTO) {
        exerciseService.updateExerciseLabel(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @Tag(name = "Admin")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> deleteExercise(@PathVariable UUID id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
