package com.padimasso.autocasting.application.employer.dto.request;

import com.padimasso.autocasting.application.shared.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record EmployerBasicInfoPatchRequest(
    @Size(max = 255, message = "employer.company_name_max_length")
    JsonNullable<String> companyName,
    @Size(max = 255, message = "employer.tax_number_max_length")
    @Pattern(regexp = "^[\\w\\-\\s]+$", message = "employer.tax_number_format")
    JsonNullable<String> taxNumber,
    UUID companyTypeId,
    @Email(message = "employer.company_email_invalid")
    @Size(max = 255, message = "employer.company_email_max_length")
    JsonNullable<String> companyEmail,
    @Size(max = 255, message = "employer.image_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    JsonNullable<String> imageUrl,
    @Size(max = 255, message = "employer.address_max_length")
    JsonNullable<String> address,
    @Size(max = 255, message = "employer.website_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    JsonNullable<String> websiteUrl,
    @Size(max = 255, message = "employer.about_max_length")
    JsonNullable<String> about
) {
}
