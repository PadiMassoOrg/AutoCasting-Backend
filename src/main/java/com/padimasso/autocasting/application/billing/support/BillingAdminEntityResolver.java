package com.padimasso.autocasting.application.billing.support;

import com.padimasso.autocasting.application.billing.model.BillableItemDiscountEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;
import com.padimasso.autocasting.application.billing.model.BillingDiscountEntity;
import com.padimasso.autocasting.application.billing.repository.BillableItemDiscountRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemPriceRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemRepository;
import com.padimasso.autocasting.application.billing.repository.BillingDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_DISCOUNT_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_DISCOUNT_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_PRICE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class BillingAdminEntityResolver {

    private final BillableItemRepository billableItemRepository;
    private final BillableItemPriceRepository billableItemPriceRepository;
    private final BillingDiscountRepository billingDiscountRepository;
    private final BillableItemDiscountRepository billableItemDiscountRepository;

    public BillableItemEntity requireItem(UUID id) {
        return billableItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(BILLING_ITEM_NOT_FOUND));
    }

    public BillableItemPriceEntity requirePrice(UUID id) {
        return billableItemPriceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(BILLING_PRICE_NOT_FOUND));
    }

    public BillingDiscountEntity requireDiscount(UUID id) {
        return billingDiscountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(BILLING_DISCOUNT_NOT_FOUND));
    }

    public BillableItemDiscountEntity requireItemDiscount(UUID id) {
        return billableItemDiscountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(BILLING_ITEM_DISCOUNT_NOT_FOUND));
    }
}
