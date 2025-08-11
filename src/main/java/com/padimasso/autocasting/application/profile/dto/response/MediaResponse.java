package com.padimasso.autocasting.application.profile.dto.response;

import java.util.List;
import java.util.UUID;

public record MediaResponse(
    UUID id,
    String headshotImageUrl,
    String fullBodyImageUrl,
    List<String> otherPicturesUrl,
    String introductionVideoUrl,
    String showReelVideoUrl
) {
}
