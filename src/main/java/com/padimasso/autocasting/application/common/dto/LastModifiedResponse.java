package com.padimasso.autocasting.application.common.dto;

import java.time.LocalDateTime;

public record LastModifiedResponse(
    LocalDateTime modifiedAt
) {
}
