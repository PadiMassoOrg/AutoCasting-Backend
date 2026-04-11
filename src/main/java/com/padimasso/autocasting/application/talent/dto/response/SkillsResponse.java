package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDateTime;
import java.util.Set;

public record SkillsResponse(
    Set<SiteMetadataObject> skills,
    LocalDateTime modifiedAt
) {
}
