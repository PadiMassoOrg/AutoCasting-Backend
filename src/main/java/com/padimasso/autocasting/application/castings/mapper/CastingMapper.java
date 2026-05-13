package com.padimasso.autocasting.application.castings.mapper;

import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;

@Component
public class CastingMapper {

    public CastingResponse toCastingResponse(
        CastingEntity casting,
        CastingEmployerInfoResponse employerInfo,
        boolean publishable
    ) {
        if (casting == null) return null;

        List<CastingRoleResponse> roles = casting.getRoles() == null
            ? List.of()
            : casting.getRoles().stream()
                .filter(role -> role != null && !role.isDeleted())
                .map(this::toRoleResponse)
                .toList();

        return new CastingResponse(
            casting.getId(),
            casting.getDefaultCode(),
            mapToSiteMetadataObject(casting.getStatus()),
            employerInfo,
            casting.getTitle(),
            mapToSiteMetadataObject(casting.getProjectType()),
            mapToSiteMetadataObject(casting.getCastingModality()),
            casting.getLocationText(),
            casting.getApplicationDeadline(),
            casting.getHasWardrobeFitting(),
            casting.getWardrobeFittingText(),
            casting.getShootingStartDate(),
            casting.getShootingEndDate(),
            casting.getDescription(),
            roles,
            publishable,
            maxModifiedAt(
                casting.getModifiedAt(),
                roles.stream().map(CastingRoleResponse::modifiedAt).toArray(LocalDateTime[]::new)
            )
        );
    }

    public CastingEmployerInfoResponse toCastingEmployerInfoResponse(
        EmployerProfileEntity employerProfile,
        Long totalCastings,
        LocalDate memberSince
    ) {
        if (employerProfile == null) return null;

        EmployerBasicInfoEntity bi = employerProfile.getBasicInfo();

        return new CastingEmployerInfoResponse(
            employerProfile.getId(),
            bi != null ? bi.getCompanyName() : null,
            mapToSiteMetadataObject(bi != null ? bi.getCompanyType() : null),
            bi != null ? bi.getImageUrl() : null,
            bi != null ? TalentProfileMapper.toSocialMediaResponse(
                bi.getSocialMediaLinks() == null ? List.of() : bi.getSocialMediaLinks().stream().toList()
            ) : null,
            totalCastings,
            memberSince,
            bi != null ? bi.getWebsiteUrl() : null
        );
    }

    public CastingCardResponse toCardResponse(CastingEntity casting, List<String> allowedStatusCodes) {
        if (casting == null) return null;

        return new CastingCardResponse(
            casting.getId(),
            casting.getTitle(),
            casting.getDefaultCode(),
            casting.getCreatedAt() != null ? casting.getCreatedAt().toLocalDate() : null,
            casting.getApplicationDeadline(),
            mapToSiteMetadataObject(casting.getProjectType()),
            mapToSiteMetadataObject(casting.getStatus()),
            allowedStatusCodes == null ? List.of() : allowedStatusCodes
        );
    }

    public CastingRolePublicCardResponse toPublicRoleCardResponse(CastingRoleEntity role) {
        if (role == null || role.getCasting() == null) return null;

        var casting = role.getCasting();
        var employerProfile = casting.getEmployerProfile();
        var employerBasicInfo = employerProfile != null ? employerProfile.getBasicInfo() : null;

        List<SiteMetadataObject> professions = role.getProfessions() == null
            ? List.of()
            : role.getProfessions().stream().map(TalentProfileMapper::mapToSiteMetadataObject).toList();

        return new CastingRolePublicCardResponse(
            role.getId(),
            role.getRoleName(),
            employerBasicInfo != null ? employerBasicInfo.getImageUrl() : null,
            employerBasicInfo != null ? employerBasicInfo.getCompanyName() : null,
            mapToSiteMetadataObject(casting.getProjectType()),
            mapToSiteMetadataObject(casting.getCastingModality()),
            casting.getLocationText(),
            casting.getShootingStartDate(),
            casting.getShootingEndDate(),
            professions,
            mapToSiteMetadataObject(role.getRoleType()),
            mapToSiteMetadataObject(role.getGender()),
            role.getAgeMin(),
            role.getAgeMax(),
            casting.getDefaultCode()
        );
    }

    public CastingRoleEmployerCardResponse toEmployerRoleCardResponse(CastingRoleEntity role) {
        if (role == null || role.isDeleted()) return null;

        List<SiteMetadataObject> professions = role.getProfessions() == null
            ? List.of()
            : role.getProfessions().stream().map(TalentProfileMapper::mapToSiteMetadataObject).toList();

        List<SiteMetadataObject> skills = role.getSkills() == null
            ? List.of()
            : role.getSkills().stream().map(TalentProfileMapper::mapToSiteMetadataObject).toList();

        return new CastingRoleEmployerCardResponse(
            role.getId(),
            role.getCasting() != null ? role.getCasting().getId() : null,
            role.getRoleName(),
            mapToSiteMetadataObject(role.getGender()),
            role.getAgeMin(),
            role.getAgeMax(),
            professions,
            mapToSiteMetadataObject(role.getRoleType()),
            skills,
            toRoleRemunerationResponse(role),
            role.getModifiedAt()
        );
    }

    public CastingRoleResponse toRoleResponse(CastingRoleEntity role) {
        if (role == null || role.isDeleted()) return null;

        List<SiteMetadataObject> professions = role.getProfessions() == null
            ? List.of()
            : role.getProfessions().stream().map(TalentProfileMapper::mapToSiteMetadataObject).toList();

        List<SiteMetadataObject> skills = role.getSkills() == null
            ? List.of()
            : role.getSkills().stream().map(TalentProfileMapper::mapToSiteMetadataObject).toList();

        return new CastingRoleResponse(
            role.getId(),
            role.getCasting() != null ? role.getCasting().getId() : null,
            role.getRoleName(),
            mapToSiteMetadataObject(role.getRoleType()),
            mapToSiteMetadataObject(role.getGender()),
            role.getAgeMin(),
            role.getAgeMax(),
            role.getDescription(),
            professions,
            skills,
            toRoleRemunerationResponse(role),
            mapToSiteMetadataObject(role.getEthnicity()),
            role.getTattoo(),
            role.getPassport(),
            role.getDrivingLicense(),
            role.isRequiresAudio(),
            role.isRequiresVideo(),
            role.getRequirementDescription(),
            role.getModifiedAt()
        );
    }

    public CastingRoleRemunerationResponse toRoleRemunerationResponse(CastingRoleEntity role) {
        if (role == null || role.isDeleted()) return null;

        boolean complete = role.getPayRateType() != null
            && (
                role.getAmount() != null
                    || (role.getPayRateType().getStringCode() != null
                    && (
                        role.getPayRateType().getStringCode().endsWith(".unpaid")
                            || role.getPayRateType().getStringCode().endsWith(".cooperative")
                            || role.getPayRateType().getStringCode().endsWith(".collaborative")
                    ))
            );

        return new CastingRoleRemunerationResponse(
            complete,
            mapToSiteMetadataObject(role.getPayRateType()),
            mapToSiteMetadataObject(role.getCurrency()),
            role.getAmount(),
            role.getRemunerationNotes(),
            role.getModifiedAt()
        );
    }

    public EmployerCastingCheckoutSummaryResponse toEmployerCastingCheckoutSummaryResponse(CastingEntity casting) {
        if (casting == null) return null;

        List<EmployerCastingCheckoutRoleResponse> roles = casting.getRoles() == null
            ? List.of()
            : casting.getRoles().stream()
                .filter(role -> role != null && !role.isDeleted())
                .map(this::toEmployerCastingCheckoutRoleResponse)
                .toList();

        return new EmployerCastingCheckoutSummaryResponse(
            casting.getId(),
            casting.getDefaultCode(),
            casting.getTitle(),
            mapToSiteMetadataObject(casting.getProjectType()),
            mapToSiteMetadataObject(casting.getCastingModality()),
            casting.getApplicationDeadline(),
            roles
        );
    }

    public EmployerCastingCheckoutRoleResponse toEmployerCastingCheckoutRoleResponse(CastingRoleEntity role) {
        if (role == null || role.isDeleted()) return null;

        return new EmployerCastingCheckoutRoleResponse(
            role.getId(),
            role.getRoleName(),
            mapToSiteMetadataObject(role.getRoleType()),
            mapToSiteMetadataObject(role.getPayRateType()),
            mapToSiteMetadataObject(role.getCurrency()),
            role.getAmount()
        );
    }

    public EmployerCastingEditorResponse toEmployerCastingEditorResponse(CastingEntity casting, boolean publishable) {
        if (casting == null) return null;

        List<CastingRoleEmployerCardResponse> roles = casting.getRoles() == null
            ? List.of()
            : casting.getRoles().stream()
                .filter(role -> role != null && !role.isDeleted())
                .map(this::toEmployerRoleCardResponse)
                .toList();

        return new EmployerCastingEditorResponse(
            casting.getId(),
            casting.getDefaultCode(),
            mapToSiteMetadataObject(casting.getStatus()),
            casting.getTitle(),
            mapToSiteMetadataObject(casting.getProjectType()),
            mapToSiteMetadataObject(casting.getCastingModality()),
            casting.getLocationText(),
            casting.getApplicationDeadline(),
            casting.getHasWardrobeFitting(),
            casting.getWardrobeFittingText(),
            casting.getShootingStartDate(),
            casting.getShootingEndDate(),
            casting.getDescription(),
            roles,
            publishable,
            maxModifiedAt(
                casting.getModifiedAt(),
                roles.stream().map(CastingRoleEmployerCardResponse::modifiedAt).toArray(LocalDateTime[]::new)
            )
        );
    }

    private LocalDateTime maxModifiedAt(LocalDateTime base, LocalDateTime... values) {
        LocalDateTime max = base;
        if (values == null) return max;
        for (LocalDateTime value : values) {
            if (value != null && (max == null || value.isAfter(max))) {
                max = value;
            }
        }
        return max;
    }
}
