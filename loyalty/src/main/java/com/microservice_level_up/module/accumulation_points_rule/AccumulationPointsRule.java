package com.microservice_level_up.module.accumulation_points_rule;

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
public class AccumulationPointsRule {
    @Id
    @SequenceGenerator(name = "accumulation_points_rule_id_sequence", sequenceName = "accumulation_points_rule_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accumulation_points_rule_id_sequence")
    private Long id;

    @Column(nullable = false)
    private int pointsToEarn;

    @Column(nullable = false)
    private double dollar;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
