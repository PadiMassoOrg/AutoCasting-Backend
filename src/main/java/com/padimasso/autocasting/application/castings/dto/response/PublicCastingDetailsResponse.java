package com.padimasso.autocasting.application.castings.dto.response;

public record PublicCastingDetailsResponse(
    PublicCastingResponse casting,
    boolean alreadyApplied
) {
}
