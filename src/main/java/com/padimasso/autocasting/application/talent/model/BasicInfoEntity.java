package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
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
@Table(name = "talent_basic_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_basic_info SET deleted = true WHERE id = ?")
public class BasicInfoEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    String stageName;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private GenderOptionEntity gender;

    @Column
    LocalDate birthDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "talent_basic_info_profession",
        joinColumns = @JoinColumn(name = "talent_basic_info_id"),
        inverseJoinColumns = @JoinColumn(name = "profession_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"talent_basic_info_id", "profession_id"})
    )
    @SQLRestriction("deleted = false")
    Set<ProfessionEntity> professions = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "talent_profile_id", nullable = false, unique = true)
    TalentProfileEntity talentProfile;

}
