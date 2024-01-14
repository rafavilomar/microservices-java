package com.microservice_level_up.module.role;

import com.microservice_level_up.module.role.entity.Role;

import java.util.List;

public interface IRoleService {

    /**
     * Get and validate the existence of a role for the given id.
     *
     * @param idRole The role id.
     * @return the role found in the database.
     * @throws jakarta.persistence.EntityNotFoundException If there is no role for the given id.
     */
    Role findRoleById(long idRole);

    /**
     * Validate the existence of a role for the given id and extract the code for all role's active permissions.
     * <p>
     * System only use and validate the permission code.
     *
     * @param idRole The role id.
     * @return the list of permission codes.
     * @see #findRoleById(long)
     */
    List<String> getPermissionCodeListByRole(long idRole);

    /**
     * Get and validate the existence of a role for the given name.
     *
     * @param name The role name.
     * @return the role found in the database.
     * @throws jakarta.persistence.EntityNotFoundException If there is no role for the given name.
     */
    Role findByName(String name);
}
