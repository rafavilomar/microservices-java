package com.microservice_level_up.module.point.repository;

import com.microservice_level_up.module.point.entities.LotPoints;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotPointsRepository extends JpaRepository<LotPoints, Long> {
}
