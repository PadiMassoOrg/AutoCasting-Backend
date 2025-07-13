package com.padimasso.autocasting.model;

import com.padimasso.autocasting.auth.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity extends AuditableEntity {

    @NotBlank()
    @Column(nullable = false)
    String name;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String defaultSlug;

    @Column(unique = true)
    private String premiumSlug;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanEntity plan;

    public String getPublicSlug() {
        return plan.isAllowsCustomSlug() && hasCustomSlug()
            ? premiumSlug
            : defaultSlug;
    }

    private boolean hasCustomSlug() {
        return premiumSlug != null && !premiumSlug.isBlank();
    }

    @PrePersist
    public void generateDefaultSlug() {
        if (this.defaultSlug == null || this.defaultSlug.isBlank()) {
            this.defaultSlug = "AC-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
