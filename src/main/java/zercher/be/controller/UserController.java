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

import zercher.be.dto.user.*;
import zercher.be.response.BaseResponse;
import zercher.be.response.PageResponse;
import zercher.be.service.user.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "User")
@RequiredArgsConstructor
@RequestMapping("/api/user")
@SecurityRequirement(name = "Authentication")
public class UserController {
    private final UserService userService;

    @Tag(name = "Admin")
    @GetMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<PageResponse<UserViewAdminDTO>> searchAdmin(@ParameterObject Pageable pageable, @Valid UserSearchAdminDTO searchAdminDTO) {
        var page = userService.getViewListAdmin(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponse<>(page));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<UserListViewDTO>>> search(@Valid UserSearchDTO searchDTO) {
        var searchResults = userService.getSearchList(searchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(searchResults));
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserViewDTO>> getProfile() {
        var userView = userService.getView();
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(userView));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<BaseResponse<UserViewDTO>> getProfile(@PathVariable UUID id) {
        var userView = userService.getView(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(userView));
    }

    @Tag(name = "Admin")
    @PatchMapping("/admin/update/{id}")
    public ResponseEntity<BaseResponse<Void>> updateUserAdmin(@PathVariable UUID id, @Valid @RequestBody UserUpdateAdminDTO updateDTO) {
        userService.updateUserAdmin(id, updateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(true));
    }
}
