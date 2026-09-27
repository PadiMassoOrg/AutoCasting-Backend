package com.padimasso.autocasting.application.employer.util;

import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.shared.util.LatestModifiedAt;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;

import java.time.LocalDateTime;
import java.util.Collection;

// Employer counterpart of TalentProfileLastSaved: the latest change across the employer profile,
// its basic info and its social media links.
public final class EmployerProfileLastSaved {

    private EmployerProfileLastSaved() {
    }

    public static LocalDateTime of(EmployerProfileEntity profile, Collection<ProfileSocialMediaLinkEntity> socialMediaLinks) {
        return LatestModifiedAt.of(
            profile.getModifiedAt(),
            LatestModifiedAt.ofEntity(profile.getBasicInfo()),
            LatestModifiedAt.ofEntities(socialMediaLinks)
        );
    }
}
