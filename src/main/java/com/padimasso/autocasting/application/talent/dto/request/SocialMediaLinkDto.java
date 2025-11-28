package com.padimasso.autocasting.application.talent.dto.request;

import java.util.UUID;

public record SocialMediaLinkDto(
    UUID optionId,
    String url
) {
}
