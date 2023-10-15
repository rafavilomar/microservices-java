package com.microservice_level_up.module.role.repositories;

import com.microservice_level_up.module.role.entites.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

    boolean existsByIdAndPermissionsCode(long id, String code);
}
