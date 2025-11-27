package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "talent_social_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_social_media SET deleted = true WHERE id = ?")
public class SocialMediaEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String instagramUrl;

    @Column
    private String tikTokUrl;

    @Column
    private String linkedinUrl;

    @Column
    private String xUrl;

    @Column
    private String vimeoUrl;

    @Column
    private String imdbUrl;

    @Column
    private String behanceUrl;

    @OneToOne
    @JoinColumn(name = "talent_profile_id", nullable = false, unique = true)
    TalentProfileEntity talentProfile;
}
