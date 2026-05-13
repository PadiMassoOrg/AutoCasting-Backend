package com.padimasso.autocasting.application.applications.repository.projection;

import java.util.UUID;

public interface ApplicationRequirementSubmissionProjection {
    UUID getApplicationId();

    UUID getCastingRoleId();

    boolean getRequiresAudio();

    boolean getRequiresVideo();

    String getAudioUrl();

    String getVideoUrl();

    String getNotes();
}
