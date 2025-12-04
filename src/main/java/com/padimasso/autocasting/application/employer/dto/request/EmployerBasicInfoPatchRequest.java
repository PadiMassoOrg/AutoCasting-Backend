package com.padimasso.autocasting.application.employer.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record EmployerBasicInfoPatchRequest(
    JsonNullable<String> companyName,
    JsonNullable<String> taxNumber,
    UUID companyTypeId,
    JsonNullable<String> companyEmail,
    JsonNullable<String> imageUrl,
    JsonNullable<String> address,
    JsonNullable<String> websiteUrl,
    JsonNullable<String> about
) {
}
