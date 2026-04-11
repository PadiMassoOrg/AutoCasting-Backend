package com.padimasso.autocasting.application.talent.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SocialMediaResponse(
    List<SocialMediaLinkResponse> links,
    LocalDateTime modifiedAt
) {
}
