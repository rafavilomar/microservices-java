package com.microservice_level_up.module.user;

import com.microservice_level_up.module.role.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column
    private Long id;

    @Builder.Default
    @Column
    private boolean active = true;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(length = 100, unique = true)
    private String email;

    @ManyToOne(optional = false, fetch = EAGER)
    @JoinColumn(nullable = false)
    private Role role;

    @Builder.Default
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
