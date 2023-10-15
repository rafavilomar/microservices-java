package com.security.module.role.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Permission {
    @Id
    @SequenceGenerator(name = "permission_id_sequence", sequenceName = "permission_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_id_sequence")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
