package com.padimasso.autocasting.application.sitemetadata.dto.response;


import java.util.List;

public record SiteMetadataResponse(
    String version,
    List<SiteMetadataObject> skills,
    List<SiteMetadataObject> professions,
    List<SiteMetadataObject> colorOptions,
    List<SiteMetadataObject> dietOptions
) {}
