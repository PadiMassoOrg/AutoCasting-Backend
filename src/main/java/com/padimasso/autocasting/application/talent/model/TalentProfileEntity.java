package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SkillEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "talent_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent SET deleted = true WHERE id = ?")
public class TalentProfileEntity extends AuditableEntity {

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

    @OneToOne(mappedBy = "talentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    BasicInfoEntity basicInfo;

    @OneToOne(mappedBy = "talentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    ContactEntity contact;

    @OneToMany(
        mappedBy = "talentProfile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private java.util.Set<TalentSocialMediaLinkEntity> socialMediaLinks = new java.util.HashSet<>();

    @OneToOne(mappedBy = "talentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    MediaEntity media;

    @OneToOne(mappedBy = "talentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    CharacteristicsEntity characteristics;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "talent_skill",
        joinColumns = @JoinColumn(name = "talent_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"talent_profile_id", "skill_id"})
    )
    @SQLRestriction("deleted = false")
    private Set<SkillEntity> skills = new HashSet<>();

    @OneToMany(
        mappedBy = "talentProfile",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @SQLRestriction("deleted = false")
    private Set<CreditEntity> credits = new HashSet<>();

    @OneToMany(
        mappedBy = "talentProfile",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @SQLRestriction("deleted = false")
    private Set<EducationEntity> education = new HashSet<>();

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
