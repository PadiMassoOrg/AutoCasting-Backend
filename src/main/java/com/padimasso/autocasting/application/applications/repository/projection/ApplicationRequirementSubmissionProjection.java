package com.padimasso.autocasting.application.applications.repository.projection;

import java.util.UUID;

public interface ApplicationRequirementSubmissionProjection {
    UUID getApplicationId();

    UUID getCastingRequirementId();

    boolean getRequiresAudio();

    boolean getRequiresVideo();

    String getAudioUrl();

    String getVideoUrl();

    String getNotes();
}
