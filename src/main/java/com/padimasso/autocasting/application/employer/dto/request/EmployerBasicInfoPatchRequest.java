package com.padimasso.autocasting.application.employer.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record EmployerBasicInfoPatchRequest(
    JsonNullable<String> userName,
    JsonNullable<String> companyName,
    UUID companyTypeId,
    JsonNullable<String> email,
    JsonNullable<String> imageUrl,
    JsonNullable<String> address,
    JsonNullable<String> website,
    JsonNullable<String> about
) {
}
