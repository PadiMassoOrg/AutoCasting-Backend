package com.padimasso.autocasting.application.sitemetadata.dto.response;


import com.padimasso.autocasting.application.auth.dto.response.RoleResponse;
import com.padimasso.autocasting.application.plan.dto.PlanResponse;

import java.util.List;

public record SiteMetadataResponse(
    String version,
    List<RoleResponse> roles,
    List<PlanResponse> plans,
    List<SiteMetadataObject> skills,
    List<SiteMetadataObject> professions,
    List<SiteMetadataObject> genderOptions,
    List<SiteMetadataObject> ethnicityOptions,
    List<SiteMetadataObject> colorOptions,
    List<SiteMetadataObject> dietOptions,
    List<SiteMetadataObject> productionTypeOptions,
    List<SiteMetadataObject> socialMediaOptions,
    List<SiteMetadataObject> companyTypeOptions,
    List<SiteMetadataObject> castingCompensationTypeOptions,
    List<SiteMetadataObject> castingModalityOptions,
    List<SiteMetadataObject> castingSectionStatusOptions,
    List<SiteMetadataObject> castingStatusOptions,
    List<SiteMetadataObject> currencyOptions,
    List<SiteMetadataObject> payRateTypeOptions,
    List<SiteMetadataObject> projectTypeOptions,
    List<SiteMetadataObject> roleTypeOptions,
    List<SiteMetadataObject> castingApplicationStatusOptions
) {
}
