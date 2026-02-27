package com.padimasso.autocasting.application.castings.dto.response;

public record PublicCastingDetailsResponse(
    CastingResponse casting,
    boolean alreadyApplied
) {
}
