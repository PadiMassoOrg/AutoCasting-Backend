package com.padimasso.autocasting.application.talent.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.DietOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.EthnicityOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "talent_characteristics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE talent_characteristics SET deleted = true WHERE id = ?")
public class CharacteristicsEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "talent_profile_id", nullable = false, unique = true)
    TalentProfileEntity talentProfile;

    @Column
    private Integer heightCm;

    @ManyToOne
    @JoinColumn(name = "ethnicity_id")
    private EthnicityOptionEntity ethnicity;

    @Column
    private Integer weightKg;

    @OneToOne
    @JoinColumn(name = "hair_color_id")
    private ColorOptionEntity hairColor;

    @OneToOne
    @JoinColumn(name = "eye_color_id")
    private ColorOptionEntity eyeColor;

    @Column
    private String chestCm;

    @Column
    private String waistCm;

    @Column
    private String hipCm;

    @Column
    private String shirtSize;

    @Column
    private String pantSize;

    @Column
    private String dressSize;

    @Column
    private String shoeSize;

    @Column
    private boolean tattoo;

    @Column
    private boolean passport;

    @Column
    private boolean drivingLicense;

    @OneToOne
    @JoinColumn(name = "diet_option_id")
    private DietOptionEntity dietOption;

}
