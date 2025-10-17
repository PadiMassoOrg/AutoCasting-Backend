package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "talent_contact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_contact SET deleted = true WHERE id = ?")
public class ContactEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    String email;

    @Column
    String phoneNumber;

    @OneToOne
    @JoinColumn(name = "talent_profile_id", nullable = false, unique = true)
    TalentProfileEntity talentProfile;
}
