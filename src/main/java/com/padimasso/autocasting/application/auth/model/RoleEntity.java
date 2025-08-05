package com.padimasso.autocasting.application.auth.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String nameStringCode;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean isActive;
}

