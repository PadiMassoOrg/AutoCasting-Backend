package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "casting_requirements_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_requirements_section SET deleted = true WHERE id = ?")
public class CastingRequirementsSectionEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_id", nullable = false, unique = true)
    private CastingEntity casting;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_status_option_id")
    private CastingSectionStatusOptionEntity sectionStatus;

    @OneToMany(
        mappedBy = "castingRequirementsSection",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<CastingRequirementEntity> requirements = new HashSet<>();
}
