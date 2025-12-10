package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting SET deleted = true WHERE id = ?")
public class CastingEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_profile_id", nullable = false)
    private EmployerProfileEntity employerProfile;

    @Column(name = "default_code", unique = true, nullable = false)
    private String defaultCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_status_option_id", nullable = false)
    private CastingStatusOptionEntity status;

    @OneToOne(mappedBy = "casting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CastingBasicInfoEntity basicInfo;

    @OneToOne(mappedBy = "casting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CastingRolesSectionEntity roles;

    @OneToOne(mappedBy = "casting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CastingActingEntity acting;

    @OneToOne(mappedBy = "casting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CastingRemunerationEntity remuneration;

    @PrePersist
    @SuppressWarnings("unused")
    public void generateDefaultCode() {
        if (this.defaultCode == null || this.defaultCode.isBlank()) {
            this.defaultCode = "C-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
