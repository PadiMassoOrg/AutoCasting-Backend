package com.padimasso.autocasting.application.profile.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "profile_characteristics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE profile_characteristics SET deleted = true WHERE id = ?")
public class CharacteristicsEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String height;

    @Column
    private String weight;

    @Column
    private String hairColor;

    @Column
    private String eyes;

    @Column
    private String chestSize;

    @Column
    private String waistSize;

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

    @Column
    private String diet;

}
