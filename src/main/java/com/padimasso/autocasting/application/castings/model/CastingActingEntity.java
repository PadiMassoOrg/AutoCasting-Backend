package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingActingModeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "casting_acting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_acting SET deleted = true WHERE id = ?")
public class CastingActingEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_id", nullable = false, unique = true)
    private CastingEntity casting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_status_option_id")
    private CastingSectionStatusOptionEntity sectionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_acting_mode_option_id", nullable = false)
    private CastingActingModeOptionEntity actingMode;

    @OneToMany(
        mappedBy = "castingActing",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<CastingActingRequirementEntity> requirements = new HashSet<>();
}
