package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TalentCatalogVisibilityTest {

    private TalentProfileEntity profile(boolean deleted, boolean suspended, MediaEntity media) {
        UserEntity user = UserEntity.builder().suspended(suspended).build();
        TalentProfileEntity profile = TalentProfileEntity.builder().user(user).media(media).build();
        profile.setDeleted(deleted);
        return profile;
    }

    private MediaEntity media(String headshotImageUrl, String fullBodyImageUrl) {
        return MediaEntity.builder()
            .headshotImageUrl(headshotImageUrl)
            .fullBodyImageUrl(fullBodyImageUrl)
            .build();
    }

    @Test
    void isVisibleInCatalog_activeProfileWithBothPhotos_returnsTrue() {
        TalentProfileEntity profile = profile(false, false, media("headshot.jpg", "fullbody.jpg"));

        assertTrue(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }

    @Test
    void isVisibleInCatalog_deletedProfile_returnsFalse() {
        TalentProfileEntity profile = profile(true, false, media("headshot.jpg", "fullbody.jpg"));

        assertFalse(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }

    @Test
    void isVisibleInCatalog_suspendedUser_returnsFalse() {
        TalentProfileEntity profile = profile(false, true, media("headshot.jpg", "fullbody.jpg"));

        assertFalse(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }

    @Test
    void isVisibleInCatalog_missingHeadshot_returnsFalse() {
        TalentProfileEntity profile = profile(false, false, media(null, "fullbody.jpg"));

        assertFalse(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }

    @Test
    void isVisibleInCatalog_missingFullBody_returnsFalse() {
        TalentProfileEntity profile = profile(false, false, media("headshot.jpg", null));

        assertFalse(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }

    @Test
    void isVisibleInCatalog_blankPhotoUrls_returnsFalse() {
        TalentProfileEntity profile = profile(false, false, media("  ", ""));

        assertFalse(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }

    @Test
    void isVisibleInCatalog_noMediaRow_returnsFalse() {
        TalentProfileEntity profile = profile(false, false, null);

        assertFalse(TalentCatalogVisibility.isVisibleInCatalog(profile));
    }
}
