package com.padimasso.autocasting.application.talent.dto.response;

import java.util.UUID;

public record SocialMediaResponse(
    UUID id,
    String instagramUrl,
    String tikTokUrl,
    String linkedinUrl,
    String xUrl,
    String vimeoUrl,
    String imdbUrl,
    String behanceUrl
) {
}
