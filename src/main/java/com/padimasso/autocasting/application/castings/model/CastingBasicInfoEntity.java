package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "casting_basic_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_basic_info SET deleted = true WHERE id = ?")
public class CastingBasicInfoEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_id", nullable = false, unique = true)
    private CastingEntity casting;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_status_option_id")
    private CastingSectionStatusOptionEntity sectionStatus;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_type_option_id")
    private ProjectTypeOptionEntity projectType;

    @Column
    private String locationText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_modality_option_id")
    private CastingModalityOptionEntity castingModality;

    @Column
    private String castingModalityText;

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

    @Column
    private String description;

    @OneToMany(
        mappedBy = "castingBasicInfo",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<CastingAttachmentEntity> attachments = new HashSet<>();

}
