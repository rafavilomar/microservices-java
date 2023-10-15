package com.security.module.role.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {
    @Id
    @SequenceGenerator(name = "role_id_sequence", sequenceName = "role_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_sequence")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permissions = new ArrayList<>();
}
