package com.padimasso.autocasting.application.applications.mapper;

import com.padimasso.autocasting.application.applications.dto.response.ApplicantRequirementSubmissionRow;
import com.padimasso.autocasting.application.applications.dto.response.EmployerCastingApplicantCardResponse;
import com.padimasso.autocasting.application.applications.dto.response.TalentCastingApplicationCardResponse;
import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CastingApplicationMapper {

    public TalentCastingApplicationCardResponse toTalentCardFromEntity(CastingApplicationEntity a) {
        var role = a.getCastingRole();
        var casting = role.getCasting();
        var employerProfile = casting.getEmployerProfile();
        var employerBasicInfo = employerProfile != null ? employerProfile.getBasicInfo() : null;

        return new TalentCastingApplicationCardResponse(
            role.getRoleName(),
            casting != null ? casting.getTitle() : null,
            casting != null && casting.getProjectType() != null ? new SiteMetadataObject(
                casting.getProjectType().getId(),
                casting.getProjectType().getStringCode(),
                casting.getProjectType().getCategoryStringCode()
            ) : null,
            casting != null && casting.getCastingModality() != null ? new SiteMetadataObject(
                casting.getCastingModality().getId(),
                casting.getCastingModality().getStringCode(),
                casting.getCastingModality().getCategoryStringCode()
            ) : null,
            casting.getStatus() != null ? new SiteMetadataObject(
                casting.getStatus().getId(),
                casting.getStatus().getStringCode(),
                casting.getStatus().getCategoryStringCode()
            ) : null,
            role.getId().toString(),
            casting.getDefaultCode(),
            employerBasicInfo != null ? employerBasicInfo.getCompanyName() : null,
            employerBasicInfo != null ? employerBasicInfo.getImageUrl() : null,
            casting != null ? casting.getLocationText() : null,
            casting != null ? casting.getShootingStartDate() : null,
            casting != null ? casting.getShootingEndDate() : null,
            role.getGender() != null ? new SiteMetadataObject(
                role.getGender().getId(),
                role.getGender().getStringCode(),
                role.getGender().getCategoryStringCode()
            ) : null,
            role.getProfessions() != null
                ? role.getProfessions().stream()
                .map(p -> new SiteMetadataObject(
                    p.getId(),
                    p.getStringCode(),
                    p.getCategoryStringCode()
                ))
                .toList()
                : List.of(),
            role.getRoleType() != null ? new SiteMetadataObject(
                role.getRoleType().getId(),
                role.getRoleType().getStringCode(),
                role.getRoleType().getCategoryStringCode()
            ) : null
        );
    }

    public EmployerCastingApplicantCardResponse toEmployerApplicantCardFromEntity(
        CastingApplicationEntity a,
        List<SiteMetadataObject> professions,
        List<ApplicantRequirementSubmissionRow> submissions
    ) {
        var tp = a.getTalentProfile();
        var tbi = tp != null ? tp.getBasicInfo() : null;
        var contact = tp != null ? tp.getContact() : null;
        var media = tp != null ? tp.getMedia() : null;
        var role = a.getCastingRole();
        var casting = role != null ? role.getCasting() : null;
        SiteMetadataObject status = a.getStatus() != null
            ? new SiteMetadataObject(
            a.getStatus().getId(),
            a.getStatus().getStringCode(),
            a.getStatus().getCategoryStringCode()
        ) : null;

        return new EmployerCastingApplicantCardResponse(
            a.getId().toString(),
            // Talent
            tp != null ? tp.getPublicSlug() : null,
            media != null ? media.getHeadshotImageUrl() : null,
            tbi != null ? tbi.getStageName() : null,
            professions,
            contact != null ? contact.getEmail() : null,
            contact != null ? contact.getPhoneNumber() : null,
            // Casting / Role
            casting != null ? casting.getTitle() : null,
            role != null ? role.getRoleName() : null,
            role != null ? role.getId().toString() : null,
            casting != null ? casting.getDefaultCode() : null,
            // Application
            status,
            submissions
        );
    }
}
