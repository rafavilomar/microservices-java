package com.microservice_level_up.module.points_redemption_rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PointsRedemptionRule {
    @Id
    @SequenceGenerator(name = "points_redemption_rule_id_sequence", sequenceName = "points_redemption_rule_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "points_redemption_rule_id_sequence")
    private Long id;

    @Column(nullable = false)
    private int pointsToRedeem;

    @Column(nullable = false)
    private double dollar;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}
