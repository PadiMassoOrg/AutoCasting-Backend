package com.padimasso.autocasting.application.talent.dto.request;

import java.util.List;

public record SocialMediaPatchRequest(
    List<SocialMediaLinkDto> links
) {
}
