package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProfessionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SkillEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

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
    @JoinColumn(name = "casting_roles_section_id", nullable = false)
    private CastingRolesSectionEntity rolesSection;

    @Column(nullable = false)
    private boolean isComplete;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "casting_role_profession",
        joinColumns = @JoinColumn(name = "casting_role_id"),
        inverseJoinColumns = @JoinColumn(name = "profession_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"casting_role_id", "profession_id"})
    )
    private Set<ProfessionEntity> professions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "casting_role_skill",
        joinColumns = @JoinColumn(name = "casting_role_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"casting_role_id", "skill_id"})
    )
    private Set<SkillEntity> skills = new HashSet<>();

    @OneToOne(mappedBy = "castingRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CastingRoleCharacteristicsEntity characteristics;

    @OneToOne(mappedBy = "castingRole", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CastingRoleRemunerationEntity remuneration;

    @OneToMany(
        mappedBy = "castingRole",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<CastingRequirementEntity> actingRequirements = new HashSet<>();
}
