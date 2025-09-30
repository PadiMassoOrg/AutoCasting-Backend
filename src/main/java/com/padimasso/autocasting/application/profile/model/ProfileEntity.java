package com.padimasso.autocasting.application.profile.model;

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
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE profile SET deleted = true WHERE id = ?")
public class ProfileEntity extends AuditableEntity {

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

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    BasicInfoEntity basicInfo;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    ContactEntity contact;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    SocialMediaEntity socialMedia;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    MediaEntity media;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    CharacteristicsEntity characteristics;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "profile_skill",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "skill_id"})
    )
    @SQLRestriction("deleted = false")
    private Set<SkillEntity> skills = new HashSet<>();

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @SQLRestriction("deleted = false")
    private Set<CreditEntity> credits = new HashSet<>();

    @OneToMany(
        mappedBy = "profile",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
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
