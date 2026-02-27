package com.padimasso.autocasting.application.applications.model;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_application SET deleted = true WHERE id = ?")
public class CastingApplicationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_role_id", nullable = false)
    private CastingRoleEntity castingRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_profile_id", nullable = false)
    private TalentProfileEntity talentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_application_status_option_id", nullable = false)
    private CastingApplicationStatusOptionEntity status;

    @Column(name = "message", columnDefinition = "text")
    private String message;

}
