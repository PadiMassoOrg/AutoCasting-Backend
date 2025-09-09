package com.padimasso.autocasting.application.profile.repository.projection;

import java.time.Instant;
import java.util.UUID;

public interface ProfileCardRow {
    UUID getId();
    String getDefaultSlug();
    String getPremiumSlug();
    Boolean getAllowsCustomSlug();   // p.plan.allowsCustomSlug
    String getStageName();
    String getHeadshotImageUrl();
    String getEmail();
    String getPhoneNumber();
    Instant getModifiedAt();

    default String getPublicSlug() {
        return Boolean.TRUE.equals(getAllowsCustomSlug())
            && getPremiumSlug() != null && !getPremiumSlug().isBlank()
            ? getPremiumSlug()
            : getDefaultSlug();
    }
}
