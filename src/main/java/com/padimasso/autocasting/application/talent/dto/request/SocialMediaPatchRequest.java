package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record SocialMediaPatchRequest(
    @Valid
    List<SocialMediaLinkDto> links
) {
}
