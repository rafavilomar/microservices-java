package com.microservice_level_up.module.role.repository;

import com.microservice_level_up.module.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByNameAndPermissionsCode(String name, String code);

    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
