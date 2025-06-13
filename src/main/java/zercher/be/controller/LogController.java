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
import zercher.be.dto.logs.WorkoutLogCreateDTO;
import zercher.be.dto.logs.WorkoutLogViewDTO;
import zercher.be.dto.logs.WorkoutLogViewListDTO;
import zercher.be.response.BaseResponse;
import zercher.be.response.PageResponse;
import zercher.be.service.log.LogService;

import java.util.UUID;

@RestController
@Tag(name = "Log")
@RequiredArgsConstructor
@RequestMapping("/api/log")
@SecurityRequirement(name = "Authentication")
public class LogController {
    private final LogService logService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Void>> createWorkoutLog(@Valid @RequestBody WorkoutLogCreateDTO createDTO) {
        logService.createWorkoutLog(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<WorkoutLogViewDTO>> getWorkoutLog(@PathVariable UUID id) {
        var workoutLog = logService.getWorkoutLogById(id);
        return new ResponseEntity<>(new BaseResponse<>(workoutLog), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<WorkoutLogViewListDTO>> getWorkoutLogList(@ParameterObject Pageable pageable) {
        var page = logService.getWorkoutLogList(pageable);
        return new ResponseEntity<>(new PageResponse<>(page), HttpStatus.OK);
    }

    @Tag(name = "Admin")
    @GetMapping("/admin/list/{userId}")
    public ResponseEntity<PageResponse<WorkoutLogViewListDTO>> getWorkoutLogListAdmin(@PathVariable UUID userId, @ParameterObject Pageable pageable) {
        var page = logService.getWorkoutLogListAdmin(userId, pageable);
        return new ResponseEntity<>(new PageResponse<>(page), HttpStatus.OK);
    }

    // update logs

    @Tag(name = "Admin")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<BaseResponse<Void>> deleteWorkoutLogAdmin(@PathVariable UUID id) {
        logService.deleteWorkoutLog(id);
        return new ResponseEntity<>(new BaseResponse<>(true), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteWorkoutLog(@PathVariable UUID id) {
        if (!logService.workoutLogBelongsToUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse<>("workoutLogDoesNotBelongToUser"));
        }
        logService.deleteWorkoutLog(id);
        return new ResponseEntity<>(new BaseResponse<>(true), HttpStatus.OK);
    }
}
