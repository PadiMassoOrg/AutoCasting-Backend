package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "talent_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_media SET deleted = true WHERE id = ?")
public class MediaEntity  extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    String headshotImageUrl;

    @Column
    String fullBodyImageUrl;

    @OneToOne
    @JoinColumn(name = "talent_profile_id", nullable = false, unique = true)
    TalentProfileEntity talentProfile;

    @Column
    String introductionVideoUrl;

    @Column
    String showReelVideoUrl;
    @ElementCollection
    @CollectionTable(
        name = "talent_media_other_pictures_url",
        joinColumns = @JoinColumn(name = "talent_media_id")
    )
    @OrderColumn(name = "idx")
    @Column(name = "url")
    private List<String> otherPicturesUrl = new ArrayList<>();
}

