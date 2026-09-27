package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.shared.util.LatestModifiedAt;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;

import java.time.LocalDateTime;
import java.util.Collection;

// The "Último guardado" timestamp the talent sees in their Frontend dashboard: the latest change
// across the profile and every section they can edit.
public final class TalentProfileLastSaved {

    private TalentProfileLastSaved() {
    }

    public static LocalDateTime of(TalentProfileEntity profile, Collection<ProfileSocialMediaLinkEntity> socialMediaLinks) {
        return LatestModifiedAt.of(
            profile.getModifiedAt(),
            LatestModifiedAt.ofEntity(profile.getBasicInfo()),
            LatestModifiedAt.ofEntity(profile.getContact()),
            LatestModifiedAt.ofEntity(profile.getMedia()),
            LatestModifiedAt.ofEntity(profile.getCharacteristics()),
            LatestModifiedAt.ofEntities(profile.getCredits()),
            LatestModifiedAt.ofEntities(profile.getEducation()),
            LatestModifiedAt.ofEntities(socialMediaLinks)
        );
    }
}
