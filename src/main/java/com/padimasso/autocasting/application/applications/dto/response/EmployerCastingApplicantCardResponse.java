package com.padimasso.autocasting.application.applications.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;

public record EmployerCastingApplicantCardResponse(
    String applicationId,
    String talentPublicSlug,
    String talentHeadshotImageUrl,
    String talentStageName,
    List<SiteMetadataObject> talentProfessions,
    String talentEmail,
    String talentPhoneNumber,
    String castingTitle,
    String castingRoleName,
    String castingRoleId,
    String castingSlug,
    SiteMetadataObject applicationStatus,
    List<ApplicantRequirementSubmissionRow> requirementSubmissions
) {
}
