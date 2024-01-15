package com.microservice_level_up.module.role;

import com.microservice_level_up.module.role.entity.Permission;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.role.repository.PermissionRepository;
import com.microservice_level_up.module.role.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @InjectMocks
    private RoleService underTest;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findRoleById_ShouldBeOk() {
        long idRole = 1;
        Role expectedResponse = Role.builder().id(idRole).name("Role").createdAt(LocalDateTime.now()).build();

        when(roleRepository.findById(idRole)).thenReturn(Optional.of(expectedResponse));

        Role actualResponse = underTest.findRoleById(idRole);

        assertEquals(expectedResponse, actualResponse);

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }

    @Test
    void findRoleById_NotFound() {
        long idRole = 1;

        when(roleRepository.findById(idRole)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> underTest.findRoleById(idRole));

        assertEquals(exception.getMessage(), "There is not role for ID: " + idRole);

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }

    @Test
    void getPermissionCodeListByRole() {
        long idRole = 1;
        List<String> expectedResponse = List.of("CODE1", "CODE2");

        Role role = Role.builder()
                .id(idRole)
                .name("Role")
                .createdAt(LocalDateTime.now())
                .permissions(List.of(
                        Permission.builder().id(1L).code("CODE1").build(),
                        Permission.builder().id(2L).code("CODE2").build()))
                .build();
        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));

        List<String> actualResponse = underTest.getPermissionCodeListByRole(idRole);

        assertEquals(expectedResponse, actualResponse);

        verify(roleRepository, times(1)).findById(idRole);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }

    @Test
    void findByName_ShouldBeOk() {
        String roleName = "Role";
        Role expectedResponse = Role.builder().id(1L).name(roleName).createdAt(LocalDateTime.now()).build();

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(expectedResponse));

        Role actualResponse = underTest.findByName(roleName);

        assertEquals(expectedResponse, actualResponse);

        verify(roleRepository, times(1)).findByName(roleName);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }

    @Test
    void findByName_NotFound() {
        String roleName = "Role";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> underTest.findByName(roleName));

        assertEquals(exception.getMessage(), "There is not role for name: " + roleName);

        verify(roleRepository, times(1)).findByName(roleName);
        verifyNoMoreInteractions(roleRepository, permissionRepository);
    }
}