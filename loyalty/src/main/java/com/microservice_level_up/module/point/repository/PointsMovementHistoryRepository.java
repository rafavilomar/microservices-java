package com.microservice_level_up.module.point.repository;

import com.microservice_level_up.module.point.entities.PointsMovementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsMovementHistoryRepository extends JpaRepository<PointsMovementHistory, Long> {
}
