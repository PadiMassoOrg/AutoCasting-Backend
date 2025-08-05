package com.padimasso.autocasting.application.sitemetadata.dto.response;

import java.util.UUID;

public record SiteMetadataObject(
    UUID id,
    String stringCode
) {}
