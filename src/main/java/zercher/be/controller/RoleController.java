package zercher.be.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zercher.be.dto.role.RoleLimitUpdateDTO;
import zercher.be.dto.role.RoleLimitViewDTO;
import zercher.be.response.BaseResponse;
import zercher.be.service.role.RoleService;

import java.util.List;

@RestController
@Tag(name = "Role")
@RequiredArgsConstructor
@RequestMapping("/api/role")
@SecurityRequirement(name = "Authentication")
public class RoleController {
    private final RoleService roleService;

    @Tag(name = "Admin")
    @GetMapping("/limit")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<RoleLimitViewDTO>> getRoleLimit() {
        var roleLimits = roleService.getRoleLimits();
        return new ResponseEntity<>(roleLimits, HttpStatus.OK);
    }

    @Tag(name = "Admin")
    @PutMapping("/limit/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> updateRoleLimit(@PathVariable Long roleId, @Valid @RequestBody RoleLimitUpdateDTO updateDTO) {
        roleService.updateRoleLimit(roleId, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
