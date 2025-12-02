package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "social_media_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE social_media_option SET deleted = true WHERE id = ?")
public class SocialMediaOptionEntity extends SiteMetadataBase {

    @Column(name = "technical_key", nullable = false)
    private String technicalKey;  // "instagram", "tiktok", "x", etc.
}
