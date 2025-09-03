package com.padimasso.autocasting.application.profile.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profile_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE profile_media SET deleted = true WHERE id = ?")
public class MediaEntity  extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    String headshotImageUrl;

    @Column
    String fullBodyImageUrl;

    @ElementCollection
    @OrderColumn(name = "idx")
    @Column(name = "url")
    private List<String> otherPicturesUrl = new ArrayList<>();

    @Column
    String introductionVideoUrl;

    @Column
    String showReelVideoUrl;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    ProfileEntity profile;
}

