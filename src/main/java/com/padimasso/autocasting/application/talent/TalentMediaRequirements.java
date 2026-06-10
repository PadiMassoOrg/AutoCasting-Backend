package com.padimasso.autocasting.application.talent;

import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.model.MediaEntity;

public final class TalentMediaRequirements {

    private TalentMediaRequirements() {
    }

    public static boolean hasRequiredPhotos(MediaEntity media) {
        if (media == null) return false;
        return hasRequiredPhotos(media.getHeadshotImageUrl(), media.getFullBodyImageUrl());
    }

    public static boolean hasRequiredPhotos(String headshotImageUrl, String fullBodyImageUrl) {
        return TextNormalizer.normalizeNullable(headshotImageUrl) != null
            && TextNormalizer.normalizeNullable(fullBodyImageUrl) != null;
    }
}
