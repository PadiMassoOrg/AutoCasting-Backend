package com.padimasso.autocasting.application.profile.repository.projection;

import java.util.UUID;

public interface ProfessionRow {
    UUID getProfileId();
    UUID getId();
    String getStringCode();
}
