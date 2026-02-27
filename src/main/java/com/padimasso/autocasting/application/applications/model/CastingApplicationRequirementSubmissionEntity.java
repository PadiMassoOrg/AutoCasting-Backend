package com.padimasso.autocasting.application.applications.model;

import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting_application_requirement_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_application_requirement_submission SET deleted = true WHERE id = ?")
public class CastingApplicationRequirementSubmissionEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CastingApplicationEntity application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_requirement_id", nullable = false)
    private CastingRequirementEntity castingRequirement;

    @Column(name = "audio_url", columnDefinition = "text")
    private String audioUrl;

    @Column(name = "video_url", columnDefinition = "text")
    private String videoUrl;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;
}
