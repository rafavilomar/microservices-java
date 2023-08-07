package com.microservice_level_up.module.point.repository;

import com.microservice_level_up.module.point.entities.LotPoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotPointsRepository extends JpaRepository<LotPoints, Long> {

    Optional<LotPoints> findByIdCustomerAndStatus(long idCustomer, boolean status);
}
