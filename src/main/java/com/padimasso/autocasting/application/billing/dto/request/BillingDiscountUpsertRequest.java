package com.padimasso.autocasting.application.billing.dto.request;

import com.padimasso.autocasting.application.billing.model.BillingDiscountType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record BillingDiscountUpsertRequest(
    @NotBlank(message = "general.required_one")
    @Size(max = 100, message = "general.invalid_parameter")
    String code,
    @NotBlank(message = "general.required_one")
    @Size(max = 255, message = "general.invalid_parameter")
    String stringCode,
    String description,
    @NotNull(message = "general.required_one")
    BillingDiscountType discountType,
    @Min(value = 1, message = "general.invalid_parameter")
    @Max(value = 10000, message = "general.invalid_parameter")
    Integer percentageBps,
    @PositiveOrZero(message = "general.invalid_parameter")
    Long amountMinor,
    @Size(min = 3, max = 3, message = "general.invalid_parameter")
    String currencyCode,
    Boolean stackable,
    Boolean active,
    OffsetDateTime startsAt,
    OffsetDateTime endsAt
) {
}
