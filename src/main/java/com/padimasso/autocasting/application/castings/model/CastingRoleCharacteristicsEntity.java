package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.DietOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.EthnicityOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "casting_role_characteristics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_role_characteristics SET deleted = true WHERE id = ?")
public class CastingRoleCharacteristicsEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "casting_role_id", nullable = false, unique = true)
    private CastingRoleEntity castingRole;

    @Column
    private Integer heightCm;

    @ManyToOne
    @JoinColumn(name = "ethnicity_id")
    private EthnicityOptionEntity ethnicity;

    @Column
    private Integer weightKg;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hair_color_id")
    private ColorOptionEntity hairColor;

    @OneToOne(fetch = FetchType.LAZY)
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
    private Boolean tattoo;

    @Column
    private Boolean passport;

    @Column
    private Boolean drivingLicense;

    @ManyToOne
    @JoinColumn(name = "diet_option_id")
    private DietOptionEntity dietOption;

}
