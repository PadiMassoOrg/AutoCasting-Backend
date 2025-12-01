package com.padimasso.autocasting.application.talent.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public record MediaPatchRequest(
    JsonNullable<String> headshotImageUrl,
    JsonNullable<String> fullBodyImageUrl,
    List<OtherPicturePatch> otherPictures,
    JsonNullable<String> introductionVideoUrl,
    JsonNullable<String> showReelVideoUrl
) {
}
