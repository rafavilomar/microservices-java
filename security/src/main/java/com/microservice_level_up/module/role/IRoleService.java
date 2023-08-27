package com.microservice_level_up.module.role;

import com.microservice_level_up.module.role.dto.NewRoleDto;
import com.microservice_level_up.module.role.dto.RoleAndPermissionDto;
import com.microservice_level_up.module.role.dto.RoleResponseDto;
import com.microservice_level_up.module.role.dto.UpdateRoleDto;
import com.microservice_level_up.module.role.entites.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoleService {
    void create(NewRoleDto newRole);

    void update(UpdateRoleDto roleUpdated);

    RoleResponseDto findById(long id);

    Role findByIdEntity(long id);

    Page<RoleResponseDto> findAll(Pageable pageable);

    void addPermission(RoleAndPermissionDto roleAndPermission);

    void removePermission(RoleAndPermissionDto roleAndPermission);
}
