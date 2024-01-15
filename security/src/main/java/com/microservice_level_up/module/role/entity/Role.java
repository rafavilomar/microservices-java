package com.microservice_level_up.module.role.entity;

import com.microservice_level_up.module.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column
    private Long id;

    @Builder.Default
    @Column
    private boolean active = true;

    @OneToOne(optional = false, fetch = EAGER)
    @JoinColumn(nullable = false)
    private User updatedUser;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Builder.Default
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "id_role"),
            inverseJoinColumns = @JoinColumn(name = "id_permission"))
    private List<Permission> permissions = new ArrayList<>();

    @OneToOne(optional = false, fetch = EAGER)
    @JoinColumn(nullable = false)
    private User createdUser;

    @Builder.Default
    @OneToMany(mappedBy = "role")
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
