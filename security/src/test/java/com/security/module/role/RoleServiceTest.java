package com.security.module.role;

import com.security.error.exception.ConflictException;
import com.security.module.role.RoleService;
import com.security.module.role.dto.*;
import com.security.module.role.entites.Permission;
import com.security.module.role.entites.Role;
import com.security.module.role.repositories.PermissionRepository;
import com.security.module.role.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @InjectMocks
    private RoleService underTest;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldBeOk() {
        long expectedResponse = 1;
        String roleName = "Role";
        NewRoleDto newRole = NewRoleDto.builder()
                .name(roleName)
                .description("description")
                .build();

        when(roleRepository.save(any(Role.class))).thenReturn(Role.builder().id(expectedResponse).build());

        long actualResponse = underTest.create(newRole);

        assertEquals(expectedResponse, actualResponse);

        verify(roleRepository, times(1)).save(any(Role.class));
        verify(roleRepository, times(1)).existsByName(roleName);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void create_DuplicatedName() {
        String roleName = "Role";
        NewRoleDto newRole = NewRoleDto.builder()
                .name(roleName)
                .description("description")
                .build();

        when(roleRepository.existsByName(roleName)).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> underTest.create(newRole));

        assertEquals("There is another role with this name: " + roleName, exception.getMessage());

        verify(roleRepository, times(1)).existsByName(roleName);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void update_ShouldBeOk() {
        String roleName = "Role";
        UpdateRoleDto updateRole = UpdateRoleDto.builder()
                .id(1L)
                .name(roleName)
                .description("Description")
                .build();
        Role role = getRole(updateRole.id());

        when(roleRepository.findById(updateRole.id())).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        long actualResponse = underTest.update(updateRole);

        assertEquals(role.getId(), actualResponse);

        verify(roleRepository, times(1)).save(any(Role.class));
        verify(roleRepository, times(1)).existsByName(roleName);
        verify(roleRepository, times(1)).findById(updateRole.id());
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void update_UpdateAdminRole() {
        long idRole = 1L;
        UpdateRoleDto newRole = UpdateRoleDto.builder()
                .id(idRole)
                .name("Role")
                .description("Description")
                .build();
        Role role = getRole(idRole);
        role.setName("Administrador");

        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> underTest.update(newRole));

        assertEquals("Can't update admin role", exception.getMessage());

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void findById_ShouldBeOk() {
        long idRole = 1L;
        Role role = getRole(idRole);
        RoleResponseDto expectedResponse = getRoleResponse(idRole);

        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));

        RoleResponseDto actualResponse = underTest.findById(idRole);

        assertEquals(expectedResponse, actualResponse);

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void findByIdEntity_ShouldBeOk() {
        long idRole = 1L;
        Role role = getRole(idRole);

        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));

        Role actualResponse = underTest.findByIdEntity(idRole);

        assertEquals(role, actualResponse);

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void findByIdEntity_NotFound() {
        long idRole = 1L;

        when(roleRepository.findById(idRole)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> underTest.findByIdEntity(idRole));

        assertEquals("There is no role for this id: " + idRole, exception.getMessage());

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 1);

        List<Role> roles = List.of(
                getRole(1L),
                getRole(2L)
        );
        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());
        when(roleRepository.findAll(pageable)).thenReturn(rolePage);

        Page<RoleResponseDto> actualResponse = underTest.findAll(pageable);

        assertAll(
                "Get all roles paged",
                () -> assertTrue(actualResponse.isFirst()),
                () -> assertFalse(actualResponse.isLast()),
                () -> assertEquals(1, actualResponse.getSize()),
                () -> assertEquals(2, actualResponse.getTotalElements()),
                () -> assertEquals(2, actualResponse.getTotalPages())
        );

        verify(roleRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void addPermission_ShouldBeOk() {
        RoleAndPermissionDto roleAndPermission = new RoleAndPermissionDto(1L, "CODE");
        Role role = getRole(1L);
        Permission permission = getPermission(2L, roleAndPermission.permissionCode());

        when(roleRepository.findById(roleAndPermission.idRole())).thenReturn(Optional.of(role));
        when(permissionRepository.findByCode(roleAndPermission.permissionCode())).thenReturn(Optional.of(permission));

        underTest.addPermission(roleAndPermission);

        role.getPermissions().add(permission);

        verify(roleRepository, times(1)).existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode());
        verify(roleRepository, times(1)).findById(roleAndPermission.idRole());
        verify(permissionRepository, times(1)).findByCode(roleAndPermission.permissionCode());
        verify(roleRepository, times(1)).save(role);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }

    @Test
    void addPermission_DuplicatedRelation() {
        RoleAndPermissionDto roleAndPermission = new RoleAndPermissionDto(1L, "CODE");

        when(roleRepository.existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode())).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> underTest.addPermission(roleAndPermission));

        assertEquals("This permission is already assigned to the Role", exception.getMessage());

        verify(roleRepository, times(1)).existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode());
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    @Test
    void addPermission_PermissionNotFound() {
        RoleAndPermissionDto roleAndPermission = new RoleAndPermissionDto(1L, "CODE");
        Role role = getRole(1L);

        when(roleRepository.findById(roleAndPermission.idRole())).thenReturn(Optional.of(role));
        when(permissionRepository.findByCode(roleAndPermission.permissionCode())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> underTest.addPermission(roleAndPermission));

        assertEquals(
                "Permission does not exist for: " + roleAndPermission.permissionCode(),
                exception.getMessage());

        verify(roleRepository, times(1)).existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode());
        verify(roleRepository, times(1)).findById(roleAndPermission.idRole());
        verify(permissionRepository, times(1)).findByCode(roleAndPermission.permissionCode());
        verifyNoMoreInteractions(permissionRepository, roleRepository);
    }

    @Test
    void removePermission_ShouldBeOk() {
        RoleAndPermissionDto roleAndPermission = new RoleAndPermissionDto(1L, "TEST");
        Role role = getRole(1L);

        when(roleRepository.findById(roleAndPermission.idRole())).thenReturn(Optional.of(role));
        when(roleRepository.existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode())).thenReturn(true);

        underTest.removePermission(roleAndPermission);

        role.setPermissions(new ArrayList<>());

        verify(roleRepository, times(1)).existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode());
        verify(roleRepository, times(1)).findById(roleAndPermission.idRole());
        verify(roleRepository, times(1)).save(role);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }

    @Test
    void removePermissionFromRole_RelationDoesNotExist() {
        RoleAndPermissionDto roleAndPermission = new RoleAndPermissionDto(1L, "CODE");

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> underTest.removePermission(roleAndPermission));

        assertEquals("This permission is not assigned to the Role", exception.getMessage());

        verify(roleRepository, times(1)).existsByIdAndPermissionsCode(
                roleAndPermission.idRole(),
                roleAndPermission.permissionCode());
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(permissionRepository);
    }

    private Role getRole(long idRole) {
        return Role.builder()
                .id(idRole)
                .name("Admin")
                .active(true)
                .permissions(List.of(getPermission(1L, "TEST")))
                .build();
    }

    private Permission getPermission(long idPermission, String code) {
        return Permission.builder()
                .id(idPermission)
                .code(code)
                .description("description")
                .active(true)
                .build();
    }

    private RoleResponseDto getRoleResponse(long idRole) {
        return RoleResponseDto.builder()
                .id(idRole)
                .name("Admin")
                .status(true)
                .permissions(
                        List.of(PermissionResponseDto.builder()
                                .code("TEST")
                                .description("description")
                                .build())
                )
                .build();
    }
}