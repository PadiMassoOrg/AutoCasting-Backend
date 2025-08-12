package com.padimasso.autocasting.application.profile.dto.response;

import java.util.Set;
import java.util.UUID;

public record MediaResponse(
    UUID id,
    String headshotImageUrl,
    String fullBodyImageUrl,
    Set<String> otherPicturesUrl,
    String introductionVideoUrl,
    String showReelVideoUrl
) {
}
