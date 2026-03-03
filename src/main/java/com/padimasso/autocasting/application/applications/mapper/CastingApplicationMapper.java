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
        var casting = role.getRolesSection().getCasting();
        var bi = casting.getBasicInfo();

        return new TalentCastingApplicationCardResponse(
            role.getRoleName(),
            bi != null ? bi.getTitle() : null,
            bi != null && bi.getProjectType() != null ? new SiteMetadataObject(
                bi.getProjectType().getId(),
                bi.getProjectType().getStringCode(),
                bi.getProjectType().getCategoryStringCode()
            ) : null,
            bi != null && bi.getCastingModality() != null ? new SiteMetadataObject(
                bi.getCastingModality().getId(),
                bi.getCastingModality().getStringCode(),
                bi.getCastingModality().getCategoryStringCode()
            ) : null,
            casting.getStatus() != null ? new SiteMetadataObject(
                casting.getStatus().getId(),
                casting.getStatus().getStringCode(),
                casting.getStatus().getCategoryStringCode()
            ) : null,
            role.getId().toString(),
            casting.getDefaultCode()
        );
    }

    public EmployerCastingApplicantCardResponse toEmployerApplicantCardFromEntity(
        CastingApplicationEntity a,
        List<SiteMetadataObject> professions,
        List<ApplicantRequirementSubmissionRow> submissions
    ) {
        var tp = a.getTalentProfile();
        var tbi = tp != null ? tp.getBasicInfo() : null;
        var media = tp != null ? tp.getMedia() : null;
        var role = a.getCastingRole();

        SiteMetadataObject status = a.getStatus() != null
            ? new SiteMetadataObject(a.getStatus().getId(), a.getStatus().getStringCode(), a.getStatus().getCategoryStringCode())
            : null;

        return new EmployerCastingApplicantCardResponse(
            a.getId().toString(),
            tp != null ? tp.getId().toString() : null,
            media != null ? media.getHeadshotImageUrl() : null,
            tbi != null ? tbi.getStageName() : null,
            professions,
            role != null ? role.getRoleName() : null,
            role != null ? role.getId().toString() : null,
            role != null ? role.getRolesSection().getCasting().getDefaultCode() : null,
            status,
            submissions
        );
    }
}
