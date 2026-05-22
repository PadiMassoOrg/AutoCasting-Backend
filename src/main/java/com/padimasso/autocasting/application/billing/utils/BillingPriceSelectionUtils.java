package com.padimasso.autocasting.application.billing.utils;

import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public final class BillingPriceSelectionUtils {

    private BillingPriceSelectionUtils() {
    }

    public static Optional<BillableItemPriceEntity> resolveCurrentPrice(List<BillableItemPriceEntity> prices, OffsetDateTime now) {
        return prices.stream()
            .filter(BillableItemPriceEntity::isActive)
            .filter(price -> !price.getValidFrom().isAfter(now))
            .filter(price -> price.getValidTo() == null || price.getValidTo().isAfter(now))
            .findFirst();
    }
}
