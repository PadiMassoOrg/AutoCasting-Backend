package com.padimasso.autocasting.application.talent.dto.request;

import com.padimasso.autocasting.application.shared.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public record MediaPatchRequest(
    @Size(max = 255, message = "talent.image_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    JsonNullable<String> headshotImageUrl,
    @Size(max = 255, message = "talent.image_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    JsonNullable<String> fullBodyImageUrl,
    @Valid
    List<OtherPicturePatch> otherPictures,
    @Size(max = 255, message = "talent.video_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    JsonNullable<String> introductionVideoUrl,
    @Size(max = 255, message = "talent.video_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    JsonNullable<String> showReelVideoUrl
) {
}
