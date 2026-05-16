package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_type_option_id")
    private ProjectTypeOptionEntity projectType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_modality_option_id")
    private CastingModalityOptionEntity castingModality;

    @Column
    private String locationText;

    @Column
    private LocalDate applicationDeadline;

    @Column
    private Boolean hasWardrobeFitting;

    @Column
    private String wardrobeFittingText;

    @Column
    private LocalDate shootingStartDate;

    @Column
    private LocalDate shootingEndDate;

    @Column(columnDefinition = "text")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "casting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CastingRoleEntity> roles = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "casting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CastingAttachmentEntity> attachments = new HashSet<>();

    @PrePersist
    @SuppressWarnings("unused")
    public void generateDefaultCode() {
        if (this.defaultCode == null || this.defaultCode.isBlank()) {
            this.defaultCode = "C-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
