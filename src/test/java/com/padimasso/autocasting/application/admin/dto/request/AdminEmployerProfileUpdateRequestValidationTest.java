package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaLinkDto;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The largest values the Admin employer schemas accept (employerProfileDraftSchema.ts) must also pass
 * the Backend's bean validation, so a save the Admin approves is never rejected by the server.
 */
class AdminEmployerProfileUpdateRequestValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String MAX_TEXT = "x".repeat(255);

    private static List<String> violations(EmployerBasicInfoPatchRequest basicInfo, SocialMediaPatchRequest social) {
        var request = new AdminEmployerProfileUpdateRequest("reason", basicInfo, social);
        return VALIDATOR.validate(request).stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
    }

    private static EmployerBasicInfoPatchRequest basicInfo(String companyName, String taxNumber, String email, String imageUrl, String address, String website, String about) {
        return new EmployerBasicInfoPatchRequest(
            JsonNullable.of(companyName), JsonNullable.of(taxNumber), UUID.randomUUID(), JsonNullable.of(email),
            JsonNullable.of(imageUrl), JsonNullable.of(address), JsonNullable.of(website), JsonNullable.of(about)
        );
    }

    @Test
    void everythingTheAdminAcceptsAtItsLimitsPassesBackendValidation() {
        var longUrl = "https://example.com/" + "a".repeat(255 - "https://example.com/".length());
        var email = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63) + ".com";
        var social = new SocialMediaPatchRequest(List.of(new SocialMediaLinkDto(UUID.randomUUID(), "https://instagram.com/" + "a".repeat(1000))));

        assertEquals(List.of(), violations(basicInfo(MAX_TEXT, "A-1 2".repeat(51), email, longUrl, MAX_TEXT, longUrl, MAX_TEXT), social));
    }

    @Test
    void clearedOptionalFieldsAreAccepted() {
        assertEquals(List.of(), violations(basicInfo("Acme", null, null, null, null, null, null), null));
    }

    @Test
    void theBackendLimitsTheAdminSchemasMirrorAreReal() {
        var tooLong = "x".repeat(256);

        assertTrue(violations(basicInfo(tooLong, null, null, null, null, null, null), null).stream().anyMatch(v -> v.contains("company_name_max_length")));
        assertTrue(violations(basicInfo("A", "tax!", null, null, null, null, null), null).stream().anyMatch(v -> v.contains("tax_number_format")));
        assertTrue(violations(basicInfo("A", null, "a".repeat(65) + "@x.com", null, null, null, null), null).stream().anyMatch(v -> v.contains("company_email_invalid")));
        assertTrue(violations(basicInfo("A", null, "a@" + "b".repeat(64) + ".com", null, null, null, null), null).stream().anyMatch(v -> v.contains("company_email_invalid")));
        assertTrue(violations(basicInfo("A", null, "not-an-email", null, null, null, null), null).stream().anyMatch(v -> v.contains("company_email_invalid")));
        assertTrue(violations(basicInfo("A", null, null, null, null, "example.com", null), null).stream().anyMatch(v -> v.contains("url_invalid")));
        assertTrue(violations(basicInfo("A", null, null, null, null, null, tooLong), null).stream().anyMatch(v -> v.contains("about_max_length")));
    }
}
