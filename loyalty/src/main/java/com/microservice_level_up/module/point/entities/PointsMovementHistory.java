package com.microservice_level_up.module.point.entities;

import com.microservice_level_up.module.point.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PointsMovementHistory {
    @Id
    @SequenceGenerator(name = "points_movement_history_id_sequence", sequenceName = "points_movement_history_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "points_movement_history_id_sequence")
    private Long id;

    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lot_points", nullable = false)
    private LotPoints lotPoints;
}
