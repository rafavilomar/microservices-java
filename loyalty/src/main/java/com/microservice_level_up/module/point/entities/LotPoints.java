package com.microservice_level_up.module.point.entities;

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
public class LotPoints {
    @Id
    @SequenceGenerator(name = "lot_points_id_sequence", sequenceName = "lot_points_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lot_points_id_sequence")
    private Long id;

    @Column(nullable = false)
    private long idCustomer;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime expirationDate;
}
