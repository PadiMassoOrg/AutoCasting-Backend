package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.OtherPicturePatch;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TalentMediaUrlPolicyTest {

    private static final String SUPABASE = "https://abc.supabase.co/";
    private static final String BUCKET = "profile-media-public";
    private static final UUID PROFILE = UUID.fromString("3f2b8c1e-9d4a-4b7e-8a5c-1d2e3f4a5b6c");
    private static final String ROOT = "https://abc.supabase.co/storage/v1/object/public/profile-media-public/talent/" + PROFILE + "/media/";

    private static MediaPatchRequest request(JsonNullable<String> headshot, JsonNullable<String> fullBody, List<OtherPicturePatch> others) {
        return new MediaPatchRequest(headshot, fullBody, others, JsonNullable.undefined(), JsonNullable.undefined());
    }

    private static void check(MediaPatchRequest request) {
        TalentMediaUrlPolicy.requireManagedPhotoUploads(request, SUPABASE, BUCKET, PROFILE);
    }

    @Test
    void acceptsUploadsInTheTalentsOwnSlotFolders() {
        assertDoesNotThrow(() -> check(request(
            JsonNullable.of(ROOT + "headshot/1727800000000.webp"),
            JsonNullable.of(ROOT + "fullbody/1727800000001.webp"),
            List.of(new OtherPicturePatch(1, ROOT + "other/1727800000002.webp"))
        )));
    }

    @Test
    void ignoresUntouchedPhotoFieldsAndVideos() {
        var videosOnly = new MediaPatchRequest(
            JsonNullable.undefined(), JsonNullable.undefined(), null,
            JsonNullable.of("https://youtu.be/x"), JsonNullable.of("https://vimeo.com/1")
        );

        assertDoesNotThrow(() -> check(videosOnly));
    }

    @Test
    void rejectsAnExternalUrl() {
        var external = request(JsonNullable.of("https://example.com/me.jpg"), JsonNullable.undefined(), null);

        assertThrows(ApiException.class, () -> check(external));
    }

    @Test
    void rejectsAnotherTalentsFolder() {
        String other = ROOT.replace(PROFILE.toString(), UUID.randomUUID().toString());

        assertThrows(ApiException.class, () -> check(request(JsonNullable.of(other + "headshot/1.webp"), JsonNullable.undefined(), null)));
    }

    @Test
    void rejectsAFileInTheWrongSlotFolder() {
        assertThrows(ApiException.class, () -> check(request(JsonNullable.of(ROOT + "fullbody/1.webp"), JsonNullable.undefined(), null)));
        assertThrows(ApiException.class, () -> check(request(JsonNullable.undefined(), JsonNullable.undefined(), List.of(new OtherPicturePatch(0, ROOT + "headshot/1.webp")))));
    }

    @Test
    void rejectsAnotherBucketOrAnotherProject() {
        assertThrows(ApiException.class, () -> check(request(
            JsonNullable.of(ROOT.replace("profile-media-public", "other-bucket") + "headshot/1.webp"), JsonNullable.undefined(), null)));
        assertThrows(ApiException.class, () -> check(request(
            JsonNullable.of(ROOT.replace("abc.supabase.co", "evil.example.com") + "headshot/1.webp"), JsonNullable.undefined(), null)));
    }

    @Test
    void rejectsClearingAPhotoThroughThePatch() {
        assertThrows(ApiException.class, () -> check(request(JsonNullable.of(null), JsonNullable.undefined(), null)));
        assertThrows(ApiException.class, () -> check(request(JsonNullable.undefined(), JsonNullable.undefined(), List.of(new OtherPicturePatch(0, null)))));
    }

    @Test
    void rejectsNestedPathsQueriesAndNonNumericFileNames() {
        assertFalse(TalentMediaUrlPolicy.isManagedUpload(ROOT + "headshot/a/1.webp", SUPABASE, BUCKET, PROFILE, "headshot"));
        assertFalse(TalentMediaUrlPolicy.isManagedUpload(ROOT + "headshot/1.webp?x=1", SUPABASE, BUCKET, PROFILE, "headshot"));
        assertFalse(TalentMediaUrlPolicy.isManagedUpload(ROOT + "headshot/photo.webp", SUPABASE, BUCKET, PROFILE, "headshot"));
        assertTrue(TalentMediaUrlPolicy.isManagedUpload(ROOT + "headshot/1.jpg", SUPABASE, BUCKET, PROFILE, "headshot"));
    }

    @Test
    void rejectsEverythingWhenStorageIsNotConfigured() {
        assertFalse(TalentMediaUrlPolicy.isManagedUpload(ROOT + "headshot/1.webp", "", BUCKET, PROFILE, "headshot"));
        assertFalse(TalentMediaUrlPolicy.isManagedUpload(ROOT + "headshot/1.webp", SUPABASE, null, PROFILE, "headshot"));
    }
}
