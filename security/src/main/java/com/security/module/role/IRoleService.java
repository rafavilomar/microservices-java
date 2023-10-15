package com.security.module.role;

import com.security.module.role.dto.NewRoleDto;
import com.security.module.role.dto.RoleAndPermissionDto;
import com.security.module.role.dto.RoleResponseDto;
import com.security.module.role.dto.UpdateRoleDto;
import com.security.module.role.entites.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRoleService {
    long create(NewRoleDto newRole);

    long update(UpdateRoleDto roleUpdated);

    RoleResponseDto findById(long id);

    Role findByIdEntity(long id);
    List<String> getPermissionCodeListByRole(long idRole);

    Page<RoleResponseDto> findAll(Pageable pageable);

    void addPermission(RoleAndPermissionDto roleAndPermission);

    void removePermission(RoleAndPermissionDto roleAndPermission);
}
