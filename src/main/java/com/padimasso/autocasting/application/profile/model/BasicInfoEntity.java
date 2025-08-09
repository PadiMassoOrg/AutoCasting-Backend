package com.padimasso.autocasting.application.profile.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "profile_basic_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicInfoEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    String stageName;

    @Column
    String gender;

    @Column
    LocalDate birthDate;

    @Column
    ArrayList<String> professions;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    ProfileEntity profile;

}
