package com.padimasso.autocasting.application.profile.dto.request;

import java.util.List;

public record MediaPatchRequest(
    String headshotImageUrl,
    String fullBodyImageUrl,
    List<OtherPicturePatch> otherPictures,
    String introductionVideoUrl,
    String showReelVideoUrl
) {
}
