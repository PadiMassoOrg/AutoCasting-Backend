package com.padimasso.autocasting.application.employer.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CompanyTypeOptionEntity;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "employer_basic_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE employer_basic_info SET deleted = true WHERE id = ?")
public class EmployerBasicInfoEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    String companyName;

    @Column
    String taxNumber;

    @ManyToOne
    @JoinColumn(name = "company_type_id")
    private CompanyTypeOptionEntity companyType;

    @Column
    String companyEmail;

    @Column
    String imageUrl;

    @Column
    String address;

    @Column
    String websiteUrl;

    @Column
    String about;

    @OneToMany(
        mappedBy = "employerBasicInfo",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ProfileSocialMediaLinkEntity> socialMediaLinks = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "employer_profile_id", nullable = false, unique = true)
    EmployerProfileEntity employerProfile;
}
