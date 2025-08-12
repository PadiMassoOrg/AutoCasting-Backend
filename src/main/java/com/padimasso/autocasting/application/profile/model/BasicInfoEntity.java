package com.padimasso.autocasting.application.profile.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProfessionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profile_basic_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE profile_basic_info SET deleted = true WHERE id = ?")
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "basic_info_profession",
        joinColumns = @JoinColumn(name = "basic_info_id"),
        inverseJoinColumns = @JoinColumn(name = "profession_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"basic_info_id", "profession_id"})
    )
    @SQLRestriction("deleted = false")
    Set<ProfessionEntity> professions = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    ProfileEntity profile;

}
