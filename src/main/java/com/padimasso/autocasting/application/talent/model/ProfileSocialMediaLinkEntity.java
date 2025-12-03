package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SocialMediaOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "talent_social_media_link")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_social_media_link SET deleted = true WHERE id = ?")
public class ProfileSocialMediaLinkEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_profile_id")
    private TalentProfileEntity talentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_basic_info_id")
    private EmployerBasicInfoEntity employerBasicInfo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "social_media_option_id", nullable = false)
    private SocialMediaOptionEntity option;

    @Column(nullable = false, length = 1024)
    private String url;
}
