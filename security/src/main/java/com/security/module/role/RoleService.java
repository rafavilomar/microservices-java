package com.security.module.role;

import com.security.error.exception.ConflictException;
import com.security.module.role.dto.*;
import com.security.module.role.entites.Permission;
import com.security.module.role.entites.Role;
import com.security.module.role.repositories.PermissionRepository;
import com.security.module.role.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public record RoleService(
        RoleRepository roleRepository,
        PermissionRepository permissionRepository) implements IRoleService {

    @Override
    public long create(NewRoleDto newRole) {
        log.info("Create new role {}", newRole);
        validateUniqueName(newRole.name());

        return roleRepository.save(Role.builder()
                .name(newRole.name())
                .description(newRole.description())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build()).getId();
    }

    @Override
    public long update(UpdateRoleDto roleUpdated) {
        log.info("Update role {}", roleUpdated);
        Role role = findByIdEntity(roleUpdated.id());

        validateNotAdminRole(role.getName());
        if (!role.getName().equalsIgnoreCase(roleUpdated.name()))
            validateUniqueName(roleUpdated.name());

        role.setName(roleUpdated.name());
        role.setDescription(roleUpdated.description());
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role).getId();
    }

    @Override
    public RoleResponseDto findById(long id) {
        return mapRoleResponse(findByIdEntity(id));
    }

    @Override
    public Role findByIdEntity(long id) {
        log.info("Get role by id {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no role for this id: " + id));
    }

    @Override
    public Page<RoleResponseDto> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::mapRoleResponse);
    }

    @Override
    public List<String> getPermissionCodeListByRole(long idRole) {
        log.info("Get permissions code list by role {}", idRole);
        Role role = findByIdEntity(idRole);

        return role.getPermissions()
                .stream()
                .filter(Permission::isActive)
                .map(Permission::getCode)
                .toList();
    }

    @Override
    public void addPermission(RoleAndPermissionDto roleAndPermission) {
        log.info("Add permission {} to role {}", roleAndPermission.permissionCode(), roleAndPermission.idRole());
        if (roleHasCode(roleAndPermission.idRole(), roleAndPermission.permissionCode())) {
            throw new ConflictException("This permission is already assigned to the Role");
        }

        Role role = findByIdEntity(roleAndPermission.idRole());
        validateNotAdminRole(role.getName());
        Permission permission = findPermissionByCode(roleAndPermission.permissionCode());

        List<Permission> newPermissions = new ArrayList<>(role.getPermissions());
        newPermissions.add(permission);
        role.setPermissions(newPermissions);

        roleRepository.save(role);
    }

    @Override
    public void removePermission(RoleAndPermissionDto roleAndPermission) {
        log.info("Remove permission {} from role {}", roleAndPermission.permissionCode(), roleAndPermission.idRole());
        if (!roleHasCode(roleAndPermission.idRole(), roleAndPermission.permissionCode())) {
            throw new ConflictException("This permission is not assigned to the Role");
        }

        Role role = findByIdEntity(roleAndPermission.idRole());
        validateNotAdminRole(role.getName());

        List<Permission> newPermissions = new ArrayList<>(role.getPermissions());
        newPermissions.removeIf(permission -> permission.getCode().equalsIgnoreCase(roleAndPermission.permissionCode()));
        role.setPermissions(newPermissions);

        roleRepository.save(role);
    }

    private void validateNotAdminRole(String roleName) {
        if (roleName.equals("Administrador")) {
            throw new ConflictException("Can't update admin role");
        }
    }

    private boolean roleHasCode(long idRole, String permissionCode) {
        return roleRepository.existsByIdAndPermissionsCode(idRole, permissionCode);
    }

    private Permission findPermissionByCode(String permissionCode) {
        return permissionRepository.findByCode(permissionCode)
                .orElseThrow(() -> new EntityNotFoundException("Permission does not exist for: " + permissionCode));
    }

    private void validateUniqueName(String name) {
        if (roleRepository.existsByName(name))
            throw new ConflictException("There is another role with this name: " + name);
    }

    private RoleResponseDto mapRoleResponse(Role role) {
        List<PermissionResponseDto> permissions = role.getPermissions()
                .stream()
                .map(this::mapPermissionResponse)
                .toList();

        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.isActive())
                .permissions(permissions)
                .build();
    }

    private PermissionResponseDto mapPermissionResponse(Permission permission) {
        return PermissionResponseDto.builder()
                .code(permission.getCode())
                .description(permission.getDescription())
                .build();
    }
}
