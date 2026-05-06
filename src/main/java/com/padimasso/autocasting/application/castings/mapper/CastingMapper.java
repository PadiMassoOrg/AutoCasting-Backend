package com.padimasso.autocasting.application.castings.mapper;

import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingBasicInfoResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRemunerationsSectionResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRequirementsSectionResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRolesSectionResponse;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;

@Component
@RequiredArgsConstructor
public class CastingMapper {

    public CastingResponse toCastingResponse(CastingEntity casting, CastingEmployerInfoResponse employerInfo) {
        return new CastingResponse(
            casting.getId(),
            casting.getDefaultCode(),
            mapToSiteMetadataObject(casting.getStatus()),
            employerInfo,
            toBasicInfoResponse(casting.getBasicInfo()),
            toRolesSectionResponsePublic(casting.getRoles()),
            toRequirementsSectionResponsePublic(casting.getRequirements()),
            toRemunerationResponsePublic(casting)
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

    public CastingCardResponse toCardResponse(CastingEntity c, List<String> allowedStatusCodes) {
        var bi = c.getBasicInfo();

        List<String> allowed = allowedStatusCodes != null ? allowedStatusCodes : List.of();

        return new CastingCardResponse(
            c.getId(),
            bi != null ? bi.getTitle() : null,
            c.getDefaultCode(),
            c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate() : null,
            bi != null ? bi.getApplicationDeadline() : null,
            mapToSiteMetadataObject(bi != null ? bi.getProjectType() : null),
            mapToSiteMetadataObject(c.getStatus()),
            allowed
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
            role.getRolesSection().getId(),
            role.getRoleName(),
            mapToSiteMetadataObject(role.getGender()),
            role.getAgeMin(),
            role.getAgeMax(),
            professions,
            mapToSiteMetadataObject(role.getRoleType()),
            skills,
            toRoleRemunerationResponse(role.getRemuneration()),
            maxModifiedAt(
                role.getModifiedAt(),
                role.getCharacteristics() != null ? role.getCharacteristics().getModifiedAt() : null,
                role.getRemuneration() != null ? role.getRemuneration().getModifiedAt() : null
            )
        );
    }

    // =========================
    // Basic Info
    // =========================
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
            entity.getHasWardrobeFitting(),
            entity.getWardrobeFittingText(),
            entity.getShootingStartDate(),
            entity.getShootingEndDate(),
            entity.getDescription(),
            entity.getModifiedAt()
        );
    }

    // =========================
    // Roles
    // =========================
    public CastingRolesSectionResponse toRolesSectionResponsePublic(CastingRolesSectionEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());

        List<CastingRoleResponse> roles =
            entity.getRoles() == null
                ? List.of()
                : entity.getRoles().stream()
                  .filter(r -> !isSoftDeleted(r.isDeleted()))
                  .map(this::toRoleResponse)
                  .toList();

        return new CastingRolesSectionResponse(
            entity.getId(),
            sectionStatus,
            entity.getNotes(),
            roles,
            maxModifiedAt(
                entity.getModifiedAt(),
                roles.stream().map(CastingRoleResponse::modifiedAt).toArray(LocalDateTime[]::new)
            )
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
            entity.getRolesSection().getId(),
            entity.getRoleName(),
            roleType,
            gender,
            entity.getAgeMin(),
            entity.getAgeMax(),
            entity.getDescription(),
            professions,
            characteristics,
            skills,
            toRoleRemunerationResponse(entity.getRemuneration()),
            maxModifiedAt(
                entity.getModifiedAt(),
                characteristics != null ? characteristics.modifiedAt() : null,
                entity.getRemuneration() != null ? entity.getRemuneration().getModifiedAt() : null
            )
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
            entity.getTattoo(),
            entity.getPassport(),
            entity.getDrivingLicense(),
            diet,
            entity.getModifiedAt()
        );
    }

    // =========================
    // Requirements
    // =========================
    public CastingRequirementsSectionResponse toRequirementsSectionResponsePublic(CastingRequirementsSectionEntity entity) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());

        List<CastingRequirementResponse> requirements =
            entity.getRequirements() == null
                ? List.of()
                : entity.getRequirements().stream()
                  .filter(r -> !isSoftDeleted(r.isDeleted()))
                  .map(this::toRequirementResponse)
                  .filter(Objects::nonNull)
                  .toList();

        return new CastingRequirementsSectionResponse(
            entity.getId(),
            sectionStatus,
            requirements,
            maxModifiedAt(
                entity.getModifiedAt(),
                requirements.stream().map(CastingRequirementResponse::modifiedAt).toArray(LocalDateTime[]::new)
            )
        );
    }

    public CastingRequirementResponse toRequirementResponse(CastingRequirementEntity entity) {
        if (entity == null) return null;
        if (isSoftDeleted(entity.isDeleted())) return null;
        if (entity.getCastingRole() != null && isSoftDeleted(entity.getCastingRole().isDeleted())) return null;

        UUID castingRoleId =
            entity.getCastingRole() != null ? entity.getCastingRole().getId() : null;

        return new CastingRequirementResponse(
            entity.getId(),
            castingRoleId,
            entity.getDescription(),
            entity.isRequiresAudio(),
            entity.isRequiresVideo(),
            entity.getModifiedAt()
        );
    }

    public CastingRequirementCardResponse toRequirementCardResponse(CastingRequirementEntity entity) {
        if (entity == null) return null;

        var base = toRequirementResponse(entity);
        if (base == null) return null;
        var roleName = entity.getCastingRole() != null ? entity.getCastingRole().getRoleName() : null;

        return new CastingRequirementCardResponse(
            base.id(),
            base.roleId(),
            roleName,
            base.requiresAudio(),
            base.requiresVideo(),
            base.description(),
            base.modifiedAt()
        );
    }

    // =========================
    // Remuneration
    // =========================
    public CastingRemunerationsSectionResponse toRemunerationsSectionResponse(
        CastingRemunerationEntity entity,
        List<CastingRoleRemunerationRowResponse> remunerations
    ) {
        if (entity == null) return null;

        SiteMetadataObject sectionStatus = mapToSiteMetadataObject(entity.getSectionStatus());
        SiteMetadataObject compensationType = mapToSiteMetadataObject(entity.getCompensationType());

        return new CastingRemunerationsSectionResponse(
            entity.getId(),
            sectionStatus,
            compensationType,
            entity.getNotes(),
            remunerations,
            maxModifiedAt(
                entity.getModifiedAt(),
                remunerations.stream().map(CastingRoleRemunerationRowResponse::modifiedAt).toArray(LocalDateTime[]::new)
            )
        );
    }

    public CastingRemunerationsSectionResponse toRemunerationResponsePublic(CastingEntity casting) {
        if (casting == null) return null;

        CastingRemunerationEntity section = casting.getRemuneration();
        if (section == null) return null;
        if (isSoftDeleted(section.isDeleted())) return null;

        List<CastingRoleRemunerationRowResponse> rows =
            casting.getRoles() == null || casting.getRoles().getRoles() == null
                ? List.of()
                : casting.getRoles().getRoles().stream()
                  .filter(r -> r != null && !isSoftDeleted(r.isDeleted()))
                  .map(CastingRoleEntity::getRemuneration)
                  .map(this::toRoleRemunerationRowResponse)
                  .filter(Objects::nonNull)
                  .toList();

        return toRemunerationsSectionResponse(section, rows);
    }

    public CastingRoleRemunerationResponse toRoleRemunerationResponse(CastingRoleRemunerationEntity entity) {
        if (entity == null) return null;
        if (isSoftDeleted(entity.isDeleted())) return null;

        return new CastingRoleRemunerationResponse(
            entity.getId(),
            entity.getCastingRole().getId(),
            entity.isComplete(),
            mapToSiteMetadataObject(entity.getPayRateType()),
            mapToSiteMetadataObject(entity.getCurrency()),
            entity.getAmount(),
            entity.getNotes(),
            entity.getModifiedAt()
        );
    }

    public CastingRoleRemunerationRowResponse toRoleRemunerationRowResponse(CastingRoleRemunerationEntity rr) {
        if (rr == null) return null;
        if (isSoftDeleted(rr.isDeleted())) return null;
        if (rr.getCastingRole() == null || isSoftDeleted(rr.getCastingRole().isDeleted())) return null;

        var base = toRoleRemunerationResponse(rr);
        if (base == null) return null;

        String roleName = rr.getCastingRole() != null ? rr.getCastingRole().getRoleName() : null;

        return new CastingRoleRemunerationRowResponse(
            base.id(),
            base.castingRoleId(),
            roleName,
            base.isComplete(),
            base.payRateType(),
            base.currency(),
            base.amount(),
            base.notes(),
            base.modifiedAt()
        );
    }

    // =========================
    // Checkout
    // =========================
    public EmployerCastingCheckoutSummaryResponse toEmployerCastingCheckoutSummaryResponse(CastingEntity casting) {
        if (casting == null) return null;

        var basicInfo = casting.getBasicInfo();
        var rolesSection = casting.getRoles();

        List<EmployerCastingCheckoutRoleResponse> roles =
            rolesSection == null || rolesSection.getRoles() == null
                ? List.of()
                : rolesSection.getRoles().stream()
                  .filter(role -> role != null && !isSoftDeleted(role.isDeleted()))
                  .map(this::toEmployerCastingCheckoutRoleResponse)
                  .filter(Objects::nonNull)
                  .toList();

        return new EmployerCastingCheckoutSummaryResponse(
            casting.getId(),
            casting.getDefaultCode(),
            basicInfo != null ? basicInfo.getTitle() : null,
            basicInfo != null ? mapToSiteMetadataObject(basicInfo.getProjectType()) : null,
            basicInfo != null ? mapToSiteMetadataObject(basicInfo.getCastingModality()) : null,
            basicInfo != null ? basicInfo.getApplicationDeadline() : null,
            roles
        );
    }

    public EmployerCastingCheckoutRoleResponse toEmployerCastingCheckoutRoleResponse(CastingRoleEntity role) {
        if (role == null) return null;
        if (isSoftDeleted(role.isDeleted())) return null;

        return new EmployerCastingCheckoutRoleResponse(
            role.getId(),
            role.getRoleName(),
            mapToSiteMetadataObject(role.getRoleType())
        );
    }

    private boolean isSoftDeleted(Boolean deleted) {
        return Boolean.TRUE.equals(deleted);
    }

    private LocalDateTime maxModifiedAt(LocalDateTime first, LocalDateTime... rest) {
        LocalDateTime max = first;
        if (rest == null) return max;
        for (LocalDateTime value : rest) {
            if (value != null && (max == null || value.isAfter(max))) {
                max = value;
            }
        }
        return max;
    }
}
