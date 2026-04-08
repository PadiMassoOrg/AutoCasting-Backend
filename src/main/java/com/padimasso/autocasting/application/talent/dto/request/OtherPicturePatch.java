package com.padimasso.autocasting.application.talent.dto.request;

import com.padimasso.autocasting.application.shared.validation.ValidationPatterns;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OtherPicturePatch(
    @Min(value = 0, message = "talent.picture_index_min")
    int index,
    @Size(max = 255, message = "talent.picture_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    String url
) {
}
