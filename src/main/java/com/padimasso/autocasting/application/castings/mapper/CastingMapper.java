package com.padimasso.autocasting.application.castings.mapper;

import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;

@Component
@RequiredArgsConstructor
public class CastingMapper {

    public CastingResponse toCastingResponse(
        CastingEntity casting
    ) {
        return new CastingResponse(
            casting.getId(),
            casting.getDefaultCode(),
            mapToSiteMetadataObject(casting.getStatus()),
            toBasicInfoResponse(casting.getBasicInfo()),
            toRolesSectionResponse(casting.getRoles()),
            toActingResponse(casting.getRequirements()),
            toRemunerationResponse(casting.getRemuneration())
        );
    }

    public CastingCardResponse toCardResponse(CastingEntity c) {
        var bi = c.getBasicInfo();

        return new CastingCardResponse(
            c.getId(),
            bi.getTitle(),
            c.getDefaultCode(),
            c.getCreatedAt().toLocalDate(),
            bi.getApplicationDeadline(),
            mapToSiteMetadataObject(bi.getProjectType()),
            mapToSiteMetadataObject(c.getStatus())
        );
    }

    public CastingRolePublicCardResponse toPublicRoleCardResponse(CastingRoleEntity role) {
        var rolesSection = role.getRolesSection();
        var casting = rolesSection.getCasting();
        var basicInfo = casting.getBasicInfo();
        var employerProfile = casting.getEmployerProfile();
        var employerBasicInfo = employerProfile != null ? employerProfile.getBasicInfo() : null;

        var professions =
            role.getProfessions() == null
                ? List.<SiteMetadataObject>of()
                : role.getProfessions().stream()
                .map(TalentProfileMapper::mapToSiteMetadataObject)
                .toList();

        return new CastingRolePublicCardResponse(
            role.getId(),
            role.getRoleName(),
            employerBasicInfo != null ? employerBasicInfo.getImageUrl() : null,
            employerBasicInfo != null ? employerBasicInfo.getCompanyName() : null,
            basicInfo != null ? mapToSiteMetadataObject(basicInfo.getProjectType()) : null,
            basicInfo != null ? mapToSiteMetadataObject(basicInfo.getCastingModality()) : null,
            basicInfo != null ? basicInfo.getLocationText() : null,
            basicInfo != null ? basicInfo.getShootingStartDate() : null,
            basicInfo != null ? basicInfo.getShootingEndDate() : null,
            professions,
            mapToSiteMetadataObject(role.getRoleType()),
            mapToSiteMetadataObject(role.getGender()),
            role.getAgeMin(),
            role.getAgeMax(),
            casting.getDefaultCode()
        );
    }

    public CastingRoleEmployerCardResponse toEmployerRoleCardResponse(CastingRoleEntity role) {
        var professions =
            role.getProfessions() == null
                ? List.<SiteMetadataObject>of()
                : role.getProfessions().stream()
                .map(TalentProfileMapper::mapToSiteMetadataObject)
                .toList();
        List<SiteMetadataObject> skills =
            role.getSkills() == null
                ? List.of()
                : role.getSkills().stream()
                .map(TalentProfileMapper::mapToSiteMetadataObject)
                .toList();

        return new CastingRoleEmployerCardResponse(
            role.getId(),
            role.getRoleName(),
            mapToSiteMetadataObject(role.getGender()),
            role.getAgeMin(),
            role.getAgeMax(),
            professions,
            mapToSiteMetadataObject(role.getRoleType()),
            skills
        );
    }


    // Sub Entities
    public CastingBasicInfoResponse toBasicInfoResponse(CastingBasicInfoEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());
        SiteMetadataObject projectType = mapToSiteMetadataObject(entity.getProjectType());
        SiteMetadataObject castingModality = mapToSiteMetadataObject(entity.getCastingModality());

        return new CastingBasicInfoResponse(
            entity.getId(),
            sectionStatus,
            entity.getTitle(),
            projectType,
            castingModality,
            entity.getCastingModalityText(),
            entity.getApplicationDeadline(),
            entity.isHasWardrobeFitting(),
            entity.getWardrobeFittingText(),
            entity.getShootingStartDate(),
            entity.getShootingEndDate(),
            entity.getDescription()
        );
    }

    public CastingRolesSectionResponse toRolesSectionResponse(CastingRolesSectionEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());

        List<CastingRoleResponse> roles =
            entity.getRoles() == null
                ? List.of()
                : entity.getRoles().stream()
                .map(this::toRoleResponse)
                .toList();

        return new CastingRolesSectionResponse(
            entity.getId(),
            sectionStatus,
            entity.getNotes(),
            roles
        );
    }

    public CastingRoleResponse toRoleResponse(CastingRoleEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject roleType = mapToSiteMetadataObject(entity.getRoleType());
        SiteMetadataObject gender = mapToSiteMetadataObject(entity.getGender());

        List<SiteMetadataObject> professions =
            entity.getProfessions() == null
                ? List.of()
                : entity.getProfessions().stream()
                .map(TalentProfileMapper::mapToSiteMetadataObject)
                .toList();

        List<SiteMetadataObject> skills =
            entity.getSkills() == null
                ? List.of()
                : entity.getSkills().stream()
                .map(TalentProfileMapper::mapToSiteMetadataObject)
                .toList();

        CharacteristicsResponse characteristics =
            toRoleCharacteristicsResponse(entity.getCharacteristics());

        return new CastingRoleResponse(
            entity.getId(),
            entity.getRoleName(),
            roleType,
            gender,
            entity.getAgeMin(),
            entity.getAgeMax(),
            entity.getDescription(),
            professions,
            characteristics,
            skills
        );
    }

    private CharacteristicsResponse toRoleCharacteristicsResponse(CastingRoleCharacteristicsEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject diet = mapToSiteMetadataObject(entity.getDietOption());
        SiteMetadataObject ethnicity = mapToSiteMetadataObject(entity.getEthnicity());
        SiteMetadataObject hairColor = mapToSiteMetadataObject(entity.getHairColor());
        SiteMetadataObject eyeColor = mapToSiteMetadataObject(entity.getEyeColor());

        return new CharacteristicsResponse(
            entity.getId(),
            entity.getHeightCm(),
            ethnicity,
            entity.getWeightKg(),
            hairColor,
            eyeColor,
            entity.getChestCm(),
            entity.getWaistCm(),
            entity.getHipCm(),
            entity.getShirtSize(),
            entity.getPantSize(),
            entity.getDressSize(),
            entity.getShoeSize(),
            entity.isTattoo(),
            entity.isPassport(),
            entity.isDrivingLicense(),
            diet
        );
    }

    public CastingRequirementsSectionResponse toActingResponse(CastingRequirementsSectionEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());

        List<CastingRequirementResponse> requirements =
            entity.getRequirements() == null
                ? List.of()
                : entity.getRequirements().stream()
                .map(this::toActingRequirementResponse)
                .toList();

        return new CastingRequirementsSectionResponse(
            entity.getId(),
            sectionStatus,
            requirements
        );
    }

    private CastingRequirementResponse toActingRequirementResponse(CastingRequirementEntity entity) {
        if (entity == null) return null;

        UUID castingRoleId =
            entity.getCastingRole() != null ? entity.getCastingRole().getId() : null;

        return new CastingRequirementResponse(
            entity.getId(),
            castingRoleId,
            entity.getDescription(),
            entity.isRequiresAudio(),
            entity.isRequiresVideo()
        );
    }

    public CastingRemunerationResponse toRemunerationResponse(CastingRemunerationEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());
        SiteMetadataObject compensationType = mapToSiteMetadataObject(entity.getCompensationType());

        return new CastingRemunerationResponse(
            entity.getId(),
            sectionStatus,
            compensationType,
            entity.isPaySameForAllRoles(),
            List.of()
        );
    }
}
