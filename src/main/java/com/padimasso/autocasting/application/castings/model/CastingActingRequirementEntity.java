package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting_acting_requirement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_acting_requirement SET deleted = true WHERE id = ?")
public class CastingActingRequirementEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private boolean isComplete;

    @ManyToOne
    @JoinColumn(name = "casting_acting_id", nullable = false)
    private CastingActingEntity castingActing;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Integer slotsCount;

    @ManyToOne
    @JoinColumn(name = "casting_role_id")
    private CastingRoleEntity castingRole;

}
