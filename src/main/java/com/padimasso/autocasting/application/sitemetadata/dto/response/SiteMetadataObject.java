package com.padimasso.autocasting.application.sitemetadata.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record SiteMetadataObject(
    UUID id,
    String stringCode,
    String category
) {}
