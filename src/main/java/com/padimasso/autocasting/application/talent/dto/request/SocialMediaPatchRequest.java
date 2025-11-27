package com.padimasso.autocasting.application.talent.dto.request;

public record SocialMediaPatchRequest(
    String instagramUrl,
    String tikTokUrl,
    String linkedinUrl,
    String xUrl,
    String vimeoUrl,
    String imdbUrl,
    String behanceUrl
) {
}
