package com.padimasso.autocasting.application.profile.dto.response;

import java.util.UUID;

public record ContactResponse(
    UUID id,
    String email,
    String phoneNumber
) {
}
