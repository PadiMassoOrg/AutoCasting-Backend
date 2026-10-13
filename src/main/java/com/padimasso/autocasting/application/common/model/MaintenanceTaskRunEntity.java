package com.padimasso.autocasting.application.common.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_task_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceTaskRunEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String taskCode;

    @Column(nullable = false)
    private LocalDateTime lastRunAt;

    @Column(nullable = false)
    private int lastDeletedObjects;
}
