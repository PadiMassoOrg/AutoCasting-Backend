package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CreditResponse(
    UUID id,
    SiteMetadataObject productionType,
    String projectName,
    String producerName,
    String role,
    String year
) {
}
