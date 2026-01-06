package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingCompensationTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting_remuneration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_remuneration SET deleted = true WHERE id = ?")
public class CastingRemunerationEntity extends AuditableEntity {

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
    @JoinColumn(name = "compensation_type_option_id", nullable = false)
    private CastingCompensationTypeOptionEntity compensationType;

    @Column
    private String notes;

}
