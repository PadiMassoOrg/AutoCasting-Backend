package com.padimasso.autocasting.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEntity extends AuditableEntity {
    
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
    private boolean allowsCustomSlug;

    @Column(nullable = false)
    private boolean isActive;
}
