package com.padimasso.autocasting.application.employer.util;

import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployerLogoUrlPolicyTest {

    private static final String SUPABASE = "https://abc.supabase.co/";
    private static final String BUCKET = "profile-media-public";
    private static final UUID PROFILE = UUID.fromString("3f2b8c1e-9d4a-4b7e-8a5c-1d2e3f4a5b6c");
    private static final String ROOT = "https://abc.supabase.co/storage/v1/object/public/profile-media-public/employer/" + PROFILE + "/logo/";

    private static EmployerBasicInfoPatchRequest request(JsonNullable<String> imageUrl) {
        return new EmployerBasicInfoPatchRequest(
            JsonNullable.undefined(), JsonNullable.undefined(), null, JsonNullable.undefined(),
            imageUrl, JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );
    }

    private static void check(JsonNullable<String> imageUrl) {
        EmployerLogoUrlPolicy.requireManagedLogoUpload(request(imageUrl), SUPABASE, BUCKET, PROFILE);
    }

    @Test
    void acceptsAnUploadInTheEmployersOwnLogoFolder() {
        assertDoesNotThrow(() -> check(JsonNullable.of(ROOT + "1727800000000.webp")));
    }

    @Test
    void ignoresARequestThatDoesNotTouchTheLogo() {
        assertDoesNotThrow(() -> check(JsonNullable.undefined()));
    }

    @Test
    void allowsClearingTheLogo() {
        assertDoesNotThrow(() -> check(JsonNullable.of(null)));
    }

    @Test
    void rejectsExternalUrlsAndOtherFoldersBucketsOrProjects() {
        assertThrows(ApiException.class, () -> check(JsonNullable.of("https://example.com/logo.png")));
        assertThrows(ApiException.class, () -> check(JsonNullable.of(ROOT.replace(PROFILE.toString(), UUID.randomUUID().toString()) + "1.webp")));
        assertThrows(ApiException.class, () -> check(JsonNullable.of(ROOT.replace("profile-media-public", "other") + "1.webp")));
        assertThrows(ApiException.class, () -> check(JsonNullable.of(ROOT.replace("abc.supabase.co", "evil.example.com") + "1.webp")));
        assertThrows(ApiException.class, () -> check(JsonNullable.of(ROOT.replace("/employer/", "/talent/") + "1.webp")));
    }

    @Test
    void rejectsNestedPathsQueriesAndNonNumericNames() {
        assertFalse(EmployerLogoUrlPolicy.isManagedUpload(ROOT + "a/1.webp", SUPABASE, BUCKET, PROFILE));
        assertFalse(EmployerLogoUrlPolicy.isManagedUpload(ROOT + "1.webp?x=1", SUPABASE, BUCKET, PROFILE));
        assertFalse(EmployerLogoUrlPolicy.isManagedUpload(ROOT + "logo.webp", SUPABASE, BUCKET, PROFILE));
        assertTrue(EmployerLogoUrlPolicy.isManagedUpload(ROOT + "1.png", SUPABASE, BUCKET, PROFILE));
    }

    @Test
    void rejectsEverythingWhenStorageIsNotConfigured() {
        assertFalse(EmployerLogoUrlPolicy.isManagedUpload(ROOT + "1.webp", "", BUCKET, PROFILE));
        assertFalse(EmployerLogoUrlPolicy.isManagedUpload(ROOT + "1.webp", SUPABASE, null, PROFILE));
    }
}
