package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting_attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_attachment SET deleted = true WHERE id = ?")
public class CastingAttachmentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_basic_info_id", nullable = false)
    private CastingBasicInfoEntity castingBasicInfo;

    @Column(nullable = false, columnDefinition = "text")
    private String fileUrl;

    @Column
    private String fileName;

    @Column
    private String fileType;

    @Column
    private Long fileSize;
}
