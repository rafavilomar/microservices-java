package com.microservice_level_up.module.role.repositories;

import com.microservice_level_up.module.role.entites.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
}
