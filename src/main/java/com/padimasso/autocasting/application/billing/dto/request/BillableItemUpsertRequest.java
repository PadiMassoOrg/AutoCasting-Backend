package com.padimasso.autocasting.application.billing.dto.request;

import com.padimasso.autocasting.application.billing.model.BillingAudience;
import com.padimasso.autocasting.application.billing.model.BillingType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BillableItemUpsertRequest(
    @NotBlank(message = "general.required_one")
    @Size(max = 100, message = "general.invalid_parameter")
    String code,
    @NotBlank(message = "general.required_one")
    @Size(max = 255, message = "general.invalid_parameter")
    String stringCode,
    String description,
    @NotEmpty(message = "general.required_one")
    Set<@NotNull(message = "general.required_one") BillingAudience> audiences,
    @NotNull(message = "general.required_one")
    BillingType billingType,
    Boolean active
) {
}
