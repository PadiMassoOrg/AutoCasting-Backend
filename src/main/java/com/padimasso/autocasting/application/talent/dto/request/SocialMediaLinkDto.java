package com.padimasso.autocasting.application.talent.dto.request;

import com.padimasso.autocasting.application.shared.validation.ValidationPatterns;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SocialMediaLinkDto(
    UUID optionId,
    @Size(max = 1024, message = "talent.social_media_url_max_length")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    String url
) {
}
