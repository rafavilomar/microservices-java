package com.microservice_level_up.module.role;

import com.microservice_level_up.module.role.entity.Permission;
import com.microservice_level_up.module.role.entity.Role;
import com.microservice_level_up.module.role.repository.PermissionRepository;
import com.microservice_level_up.module.role.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public record RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) implements IRoleService {

    @Override
    public Role findRoleById(long idRole) {
        log.info("Find role by ID {}", idRole);
        return roleRepository
                .findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("There is not role for ID: " + idRole));
    }

    @Override
    public List<String> getPermissionCodeListByRole(long idRole) {
        log.info("Get permission codes by role {}", idRole);
        return findRoleById(idRole).getPermissions().stream().map(Permission::getCode).toList();
    }

    @Override
    public Role findByName(String name) {
        log.info("Find role by name {}", name);
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("There is not role for name: " + name));
    }
}
