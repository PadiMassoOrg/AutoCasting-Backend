package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.talent.dto.response.SocialMediaLinkResponse;

import java.util.List;

public record PublicSocialMediaResponse(
    List<SocialMediaLinkResponse> links
) {
}
