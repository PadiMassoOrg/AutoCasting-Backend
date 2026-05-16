package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.EthnicityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProfessionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SkillEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "casting_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_role SET deleted = true WHERE id = ?")
public class CastingRoleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_id", nullable = false)
    private CastingEntity casting;

    @Column(nullable = false)
    private String roleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_type_option_id")
    private RoleTypeOptionEntity roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_option_id")
    private GenderOptionEntity gender;

    @Column(nullable = false)
    private Short ageMin;

    @Column(nullable = false)
    private Short ageMax;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_rate_type_option_id", nullable = false)
    private PayRateTypeOptionEntity payRateType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_option_id")
    private CurrencyOptionEntity currency;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "text")
    private String remunerationNotes;

    @Column(nullable = false)
    private boolean requiresAudio;

    @Column(nullable = false)
    private boolean requiresVideo;

    @Column(columnDefinition = "text")
    private String requirementDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ethnicity_id")
    private EthnicityOptionEntity ethnicity;

    @Column
    private Boolean tattoo;

    @Column
    private Boolean passport;

    @Column
    private Boolean drivingLicense;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "casting_role_profession",
        joinColumns = @JoinColumn(name = "casting_role_id"),
        inverseJoinColumns = @JoinColumn(name = "profession_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"casting_role_id", "profession_id"})
    )
    private Set<ProfessionEntity> professions = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "casting_role_skill",
        joinColumns = @JoinColumn(name = "casting_role_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"casting_role_id", "skill_id"})
    )
    private Set<SkillEntity> skills = new HashSet<>();
}
