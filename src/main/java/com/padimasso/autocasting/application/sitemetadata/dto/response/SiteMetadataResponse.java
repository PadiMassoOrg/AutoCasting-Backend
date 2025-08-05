package com.padimasso.autocasting.application.sitemetadata.dto.response;


import java.util.List;

public record SiteMetadataResponse(
    List<SiteMetadataObject> skills
) {}
