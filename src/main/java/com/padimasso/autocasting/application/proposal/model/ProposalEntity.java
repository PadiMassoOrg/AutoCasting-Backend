package com.padimasso.autocasting.application.proposal.model;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_type_option_id", nullable = false)
    private ProposalTypeOptionEntity type;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProposalStatus status = ProposalStatus.PENDING;

    @Column
    private String contactName;

    @Column
    private String contactEmail;

    @Column
    private String contactWhatsapp;

    @Column
    private String companyNameHint;

    @Column(columnDefinition = "text")
    private String internalNotes;

    @Column
    private LocalDateTime firstOpenedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by_user_id")
    private UserEntity claimedByUser;

    @Column
    private LocalDateTime claimedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_id", unique = true)
    private CastingEntity casting;
}
