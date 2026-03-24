package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record EmployerCastingCheckoutRoleResponse(
    UUID id,
    String roleName,
    SiteMetadataObject roleType
) {
}
