package com.padimasso.autocasting.application.employer.model;


import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "employer_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE employer_profile SET deleted = true WHERE id = ?")
public class EmployerProfileEntity extends AuditableEntity {

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

    @OneToOne(mappedBy = "employerProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    EmployerBasicInfoEntity basicInfo;

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
    @SuppressWarnings("unused")
    public void generateDefaultSlug() {
        if (this.defaultSlug == null || this.defaultSlug.isBlank()) {
            this.defaultSlug = "AC-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}

