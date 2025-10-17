package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProductionTypeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "talent_credit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_credit SET deleted = true WHERE id = ?")
public class CreditEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "production_type_id", nullable = false)
    private ProductionTypeEntity productionType;

    @Column
    String projectName;

    @Column
    String producerName;

    @Column
    String role;

    @Column
    String year;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_profile_id", nullable = false)
    private TalentProfileEntity talentProfile;
}
