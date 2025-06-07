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
import zercher.be.dto.unit.UnitCreateUpdateDTO;
import zercher.be.dto.unit.UnitViewDTO;
import zercher.be.response.BaseResponse;
import zercher.be.service.unit.UnitService;

import java.util.List;

@RestController
@Tag(name = "Unit")
@RequiredArgsConstructor
@RequestMapping("/api/unit")
@SecurityRequirement(name = "Authentication")
public class UnitController {
    private final UnitService unitService;

    @Tag(name = "Admin")
    @PostMapping("/admin/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> createUnit(@Valid @RequestBody UnitCreateUpdateDTO createDTO) {
        unitService.createUnit(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true));
    }

    @Tag(name = "Admin")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> updateUnit(@PathVariable Long id, @Valid @RequestBody UnitCreateUpdateDTO updateDTO) {
        unitService.updateUnit(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<UnitViewDTO>>> getUnits(@ParameterObject Pageable pageable) {
        var units = unitService.getUnits(pageable);
        return new ResponseEntity<>(new BaseResponse<>(units), HttpStatus.OK);
    }

    @Tag(name = "Admin")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
