package com.padimasso.autocasting.application.billing.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BillableItemDiscountUpsertRequest(
    @NotNull(message = "general.required_one")
    UUID billableItemId,
    @NotNull(message = "general.required_one")
    UUID billingDiscountId,
    @NotNull(message = "general.required_one")
    OffsetDateTime validFrom,
    OffsetDateTime validTo,
    @NotNull(message = "general.required_one")
    @Min(value = 0, message = "general.invalid_parameter")
    @Max(value = 32767, message = "general.invalid_parameter")
    Integer priority,
    Boolean active
) {
}
