package com.padimasso.autocasting.application.profile.dto.request;

import java.util.Set;

public record MediaPatchRequest(
    String headshotImageUrl,
    String fullBodyImageUrl,
    Set<String> otherPicturesUrl,
    String introductionVideoUrl,
    String showReelVideoUrl
) {
}
