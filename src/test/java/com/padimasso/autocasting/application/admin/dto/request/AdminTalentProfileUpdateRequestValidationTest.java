package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.talent.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.CharacteristicsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.CreditRequest;
import com.padimasso.autocasting.application.talent.dto.request.CreditUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.request.EducationRequest;
import com.padimasso.autocasting.application.talent.dto.request.EducationUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaLinkDto;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Admin validates before sending. These tests pin the other side of that contract: the largest
 * values the Admin schemas accept (talentProfileDraftSchema.ts) must also pass the Backend's bean
 * validation, so a save the Admin approves is never rejected by the server.
 */
class AdminTalentProfileUpdateRequestValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String MAX_TEXT = "x".repeat(255);

    private static AdminTalentProfileUpdateRequest request(
        BasicInfoPatchRequest basicInfo,
        SocialMediaPatchRequest socialMedia,
        MediaPatchRequest media,
        CharacteristicsPatchRequest characteristics,
        List<CreditUpsertRequest> credits,
        List<EducationUpsertRequest> education
    ) {
        return new AdminTalentProfileUpdateRequest("reason", basicInfo, null, socialMedia, media, characteristics, new SkillsPatchRequest(Set.of(UUID.randomUUID())), credits, education);
    }

    private static List<String> violations(AdminTalentProfileUpdateRequest request) {
        return VALIDATOR.validate(request).stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
    }

    private static CharacteristicsPatchRequest characteristics(int height, int weight, String size) {
        return new CharacteristicsPatchRequest(
            JsonNullable.of(height), UUID.randomUUID(), JsonNullable.of(weight), UUID.randomUUID(), UUID.randomUUID(),
            JsonNullable.of(size), JsonNullable.of(size), JsonNullable.of(size), JsonNullable.of(size),
            JsonNullable.of(size), JsonNullable.of(size), JsonNullable.of(size), true, false, true, UUID.randomUUID()
        );
    }

    @Test
    void everythingTheAdminAcceptsAtItsLimitsPassesBackendValidation() {
        var basicInfo = new BasicInfoPatchRequest(MAX_TEXT, UUID.randomUUID(), LocalDate.now().minusDays(1), Set.of(UUID.randomUUID(), UUID.randomUUID()));
        var longSocialUrl = "https://instagram.com/" + "a".repeat(1024 - "https://instagram.com/".length());
        var social = new SocialMediaPatchRequest(List.of(new SocialMediaLinkDto(UUID.randomUUID(), longSocialUrl)));
        var longVideoUrl = "https://youtu.be/" + "a".repeat(255 - "https://youtu.be/".length());
        var media = new MediaPatchRequest(JsonNullable.undefined(), JsonNullable.undefined(), null, JsonNullable.of(longVideoUrl), JsonNullable.of(longVideoUrl));
        var credit = new CreditUpsertRequest(null, new CreditRequest(UUID.randomUUID(), MAX_TEXT, MAX_TEXT, MAX_TEXT, "2026"));
        var education = new EducationUpsertRequest(null, new EducationRequest(MAX_TEXT, MAX_TEXT, "2026"));

        assertEquals(List.of(), violations(request(basicInfo, social, media, characteristics(300, 500, "XXX"), List.of(credit), List.of(education))));
        assertEquals(List.of(), violations(request(basicInfo, social, media, characteristics(20, 0, "999"), List.of(), List.of())));
    }

    @Test
    void nullTextFromAnUntouchedLegacyItemIsAccepted() {
        var legacyCredit = new CreditUpsertRequest(UUID.randomUUID(), new CreditRequest(UUID.randomUUID(), null, null, null, null));
        var legacyEducation = new EducationUpsertRequest(UUID.randomUUID(), new EducationRequest(null, null, null));

        assertEquals(List.of(), violations(request(null, null, null, null, List.of(legacyCredit), List.of(legacyEducation))));
    }

    @Test
    void theBackendLimitsTheAdminSchemasMirrorAreReal() {
        var tooLongSocial = new SocialMediaPatchRequest(List.of(new SocialMediaLinkDto(UUID.randomUUID(), "https://instagram.com/" + "a".repeat(1100))));
        var tooLongVideo = new MediaPatchRequest(JsonNullable.undefined(), JsonNullable.undefined(), null, JsonNullable.of("https://youtu.be/" + "a".repeat(300)), JsonNullable.undefined());
        var today = new BasicInfoPatchRequest(null, null, LocalDate.now(), null);
        var emptyYear = new CreditUpsertRequest(null, new CreditRequest(UUID.randomUUID(), "a", "b", "c", ""));

        assertTrue(violations(request(null, tooLongSocial, null, null, null, null)).stream().anyMatch(v -> v.contains("social_media_url_max_length")));
        assertTrue(violations(request(null, null, tooLongVideo, null, null, null)).stream().anyMatch(v -> v.contains("video_url_max_length")));
        assertTrue(violations(request(today, null, null, null, null, null)).stream().anyMatch(v -> v.contains("birth_date_future")));
        assertTrue(violations(request(null, null, null, null, List.of(emptyYear), null)).stream().anyMatch(v -> v.contains("year_format")));
    }
}
