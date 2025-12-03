package com.padimasso.autocasting.application.employer.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;

import java.util.UUID;

public record EmployerBasicInfoResponse(
    UUID id,
    String userName,
    String companyName,
    SiteMetadataObject companyType,
    String email,
    String imageUrl,
    String address,
    String website,
    String about,
    SocialMediaResponse socialMedia
) {
}
