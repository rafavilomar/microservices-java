package com.security.module.role;

import com.security.module.role.dto.NewRoleDto;
import com.security.module.role.dto.RoleAndPermissionDto;
import com.security.module.role.dto.RoleResponseDto;
import com.security.module.role.dto.UpdateRoleDto;
import com.security.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role")
@RestController
@RequestMapping("/api/v1/role")
@SecurityRequirement(name = "bearerAuth")
public record RoleController(IRoleService roleService) {

    @PostMapping
    @Operation(summary = "Create new role")
    @PreAuthorize("hasAuthority('ROLE')")
    public ResponseEntity<BaseResponse<Long>> createRole(@RequestBody @Valid NewRoleDto newRole) {

        Long payload = roleService.create(newRole);
        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.CREATED,
                "Created role"
        );
    }

    @PutMapping
    @Operation(summary = "Update role")
    @PreAuthorize("hasAuthority('ROLE')")
    public ResponseEntity<BaseResponse<Long>> updateRole(@RequestBody @Valid UpdateRoleDto updatedRole) {

        Long payload = roleService.update(updatedRole);
        BaseResponse<Long> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Updated role"
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by id")
    @PreAuthorize("hasAuthority('ROLE')")
    public ResponseEntity<BaseResponse<RoleResponseDto>> findById(@RequestParam("id") long id) {

        RoleResponseDto payload = roleService.findById(id);
        BaseResponse<RoleResponseDto> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Found role"
        );
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    @PreAuthorize("hasAuthority('ROLE')")
    public ResponseEntity<BaseResponse<Page<RoleResponseDto>>> findAll(
            @RequestParam("page") @Positive(message = "page: must be higher than zero") int page,
            @RequestParam("size") @Positive(message = "size: must be higher than zero") int size) {

        Page<RoleResponseDto> payload = roleService.findAll(PageRequest.of(page - 1, size));
        BaseResponse<Page<RoleResponseDto>> response = new BaseResponse<>();
        return response.buildResponseEntity(
                payload,
                HttpStatus.OK,
                "Found roles"
        );
    }

    @PutMapping("/addPermission")
    @Operation(summary = "Add new permission to role")
    @PreAuthorize("hasAuthority('ROLE')")
    public ResponseEntity<BaseResponse<Void>> addPermission(@RequestBody @Valid RoleAndPermissionDto roleAndPermission) {

        roleService.addPermission(roleAndPermission);
        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.NO_CONTENT,
                "Permission added to role"
        );
    }

    @PutMapping("/removePermission")
    @Operation(summary = "Remove permission from role")
    @PreAuthorize("hasAuthority('ROLE')")
    public ResponseEntity<BaseResponse<Void>> removePermission(@RequestBody @Valid RoleAndPermissionDto roleAndPermission) {

        roleService.removePermission(roleAndPermission);
        BaseResponse<Void> response = new BaseResponse<>();
        return response.buildResponseEntity(
                null,
                HttpStatus.NO_CONTENT,
                "Permission removed from role"
        );
    }
}
