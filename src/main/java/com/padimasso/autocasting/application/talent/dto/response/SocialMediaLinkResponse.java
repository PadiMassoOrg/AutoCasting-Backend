package com.padimasso.autocasting.application.talent.dto.response;

import java.util.UUID;

public record SocialMediaLinkResponse(
    UUID optionId,
    String stringCode,
    String url
) {
}
