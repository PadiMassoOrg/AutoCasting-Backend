package com.padimasso.autocasting.application.billing.support;

import com.padimasso.autocasting.application.billing.dto.request.BillableItemDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.model.BillingDiscountType;
import com.padimasso.autocasting.application.billing.repository.BillableItemDiscountRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemPriceRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemRepository;
import com.padimasso.autocasting.application.billing.repository.BillingDiscountRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CurrencyOptionRepository;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.UUID;

import static com.padimasso.autocasting.application.billing.utils.BillingNormalizationUtils.normalizeCode;
import static com.padimasso.autocasting.application.billing.utils.BillingNormalizationUtils.normalizeCurrency;
import static com.padimasso.autocasting.application.billing.utils.BillingNormalizationUtils.normalizeNullableCurrency;
import static com.padimasso.autocasting.application.billing.utils.BillingCurrencyMetadataUtils.toSiteMetadataStringCode;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_CURRENCY_INVALID_ISO;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_CURRENCY_NOT_SUPPORTED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_DISCOUNT_CODE_ALREADY_EXISTS;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_DISCOUNT_VALIDITY_INVALID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_DISCOUNT_VALUE_SHAPE_INVALID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_REQUIRED_ONE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_CODE_ALREADY_EXISTS;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_DISCOUNT_VALIDITY_INVALID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_DISCOUNT_WINDOW_OVERLAP;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_PRICE_VALIDITY_INVALID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_PRICE_WINDOW_OVERLAP;

@Component
@RequiredArgsConstructor
public class BillingAdminRequestValidator {

    private final BillableItemRepository billableItemRepository;
    private final BillableItemPriceRepository billableItemPriceRepository;
    private final BillingDiscountRepository billingDiscountRepository;
    private final BillableItemDiscountRepository billableItemDiscountRepository;
    private final CurrencyOptionRepository currencyOptionRepository;

    public void validateItemCreate(BillableItemUpsertRequest request) {
        requireAudiences(request);

        String normalizedCode = normalizeCode(request.code());
        if (billableItemRepository.existsByCode(normalizedCode)) {
            throw ApiException.conflict(BILLING_ITEM_CODE_ALREADY_EXISTS);
        }
    }

    public void validateItemUpdate(UUID itemId, BillableItemUpsertRequest request) {
        requireAudiences(request);

        String normalizedCode = normalizeCode(request.code());
        if (billableItemRepository.existsByCodeAndIdNot(normalizedCode, itemId)) {
            throw ApiException.conflict(BILLING_ITEM_CODE_ALREADY_EXISTS);
        }
    }

    public void validatePriceCreate(BillableItemPriceUpsertRequest request) {
        assertValidWindow(request.validFrom(), request.validTo(), BILLING_PRICE_VALIDITY_INVALID);

        String normalizedCurrency = normalizeCurrency(request.currencyCode());
        assertIsoCurrency(normalizedCurrency);
        assertSupportedCurrency(normalizedCurrency);
        boolean overlaps = hasPriceWindowOverlap(
            request.billableItemId(),
            normalizedCurrency,
            request.validFrom(),
            request.validTo(),
            null
        );
        if (overlaps) {
            throw ApiException.conflict(BILLING_PRICE_WINDOW_OVERLAP);
        }
    }

    public void validatePriceUpdate(UUID priceId, BillableItemPriceUpsertRequest request) {
        assertValidWindow(request.validFrom(), request.validTo(), BILLING_PRICE_VALIDITY_INVALID);

        String normalizedCurrency = normalizeCurrency(request.currencyCode());
        assertIsoCurrency(normalizedCurrency);
        assertSupportedCurrency(normalizedCurrency);
        boolean overlaps = hasPriceWindowOverlap(
            request.billableItemId(),
            normalizedCurrency,
            request.validFrom(),
            request.validTo(),
            priceId
        );
        if (overlaps) {
            throw ApiException.conflict(BILLING_PRICE_WINDOW_OVERLAP);
        }
    }

    public void validateDiscountCreate(BillingDiscountUpsertRequest request) {
        String normalizedCode = normalizeCode(request.code());
        if (billingDiscountRepository.existsByCode(normalizedCode)) {
            throw ApiException.conflict(BILLING_DISCOUNT_CODE_ALREADY_EXISTS);
        }

        validateDiscountShapeAndWindow(request);
    }

    public void validateDiscountUpdate(UUID discountId, BillingDiscountUpsertRequest request) {
        String normalizedCode = normalizeCode(request.code());
        if (billingDiscountRepository.existsByCodeAndIdNot(normalizedCode, discountId)) {
            throw ApiException.conflict(BILLING_DISCOUNT_CODE_ALREADY_EXISTS);
        }

        validateDiscountShapeAndWindow(request);
    }

    public void validateItemDiscountCreate(BillableItemDiscountUpsertRequest request) {
        assertValidWindow(request.validFrom(), request.validTo(), BILLING_ITEM_DISCOUNT_VALIDITY_INVALID);

        boolean overlaps = hasItemDiscountWindowOverlap(
            request.billableItemId(),
            request.billingDiscountId(),
            request.validFrom(),
            request.validTo(),
            null
        );
        if (overlaps) {
            throw ApiException.conflict(BILLING_ITEM_DISCOUNT_WINDOW_OVERLAP);
        }
    }

    public void validateItemDiscountUpdate(UUID itemDiscountId, BillableItemDiscountUpsertRequest request) {
        assertValidWindow(request.validFrom(), request.validTo(), BILLING_ITEM_DISCOUNT_VALIDITY_INVALID);

        boolean overlaps = hasItemDiscountWindowOverlap(
            request.billableItemId(),
            request.billingDiscountId(),
            request.validFrom(),
            request.validTo(),
            itemDiscountId
        );
        if (overlaps) {
            throw ApiException.conflict(BILLING_ITEM_DISCOUNT_WINDOW_OVERLAP);
        }
    }

    private void validateDiscountShapeAndWindow(BillingDiscountUpsertRequest request) {
        assertValidWindow(request.startsAt(), request.endsAt(), BILLING_DISCOUNT_VALIDITY_INVALID);

        if (request.discountType() == BillingDiscountType.PERCENTAGE) {
            boolean validPercentageShape = request.percentageBps() != null
                && request.percentageBps() > 0
                && request.percentageBps() <= 10_000
                && request.amountMinor() == null
                && normalizeNullableCurrency(request.currencyCode()) == null;
            if (!validPercentageShape) {
                throw ApiException.badRequest(BILLING_DISCOUNT_VALUE_SHAPE_INVALID);
            }
            return;
        }

        boolean validFixedAmountShape = request.amountMinor() != null
            && request.amountMinor() >= 0
            && normalizeNullableCurrency(request.currencyCode()) != null
            && request.percentageBps() == null;
        if (!validFixedAmountShape) {
            throw ApiException.badRequest(BILLING_DISCOUNT_VALUE_SHAPE_INVALID);
        }

        String normalizedCurrency = normalizeCurrency(request.currencyCode());
        assertIsoCurrency(normalizedCurrency);
        assertSupportedCurrency(normalizedCurrency);
    }

    private void assertValidWindow(OffsetDateTime start, OffsetDateTime end, String messageKey) {
        if (start != null && end != null && !end.isAfter(start)) {
            throw ApiException.badRequest(messageKey);
        }
    }

    private boolean hasPriceWindowOverlap(
        UUID billableItemId,
        String currencyCode,
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        UUID excludeId
    ) {
        if (validTo == null) {
            return billableItemPriceRepository.existsOverlappingWindow(
                billableItemId,
                currencyCode,
                validFrom,
                excludeId
            );
        }

        return billableItemPriceRepository.existsOverlappingWindowUntil(
            billableItemId,
            currencyCode,
            validFrom,
            validTo,
            excludeId
        );
    }

    private boolean hasItemDiscountWindowOverlap(
        UUID billableItemId,
        UUID billingDiscountId,
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        UUID excludeId
    ) {
        if (validTo == null) {
            return billableItemDiscountRepository.existsOverlappingWindow(
                billableItemId,
                billingDiscountId,
                validFrom,
                excludeId
            );
        }

        return billableItemDiscountRepository.existsOverlappingWindowUntil(
            billableItemId,
            billingDiscountId,
            validFrom,
            validTo,
            excludeId
        );
    }

    private void requireAudiences(BillableItemUpsertRequest request) {
        if (request.audiences() == null || request.audiences().isEmpty()) {
            throw ApiException.badRequest(GENERAL_REQUIRED_ONE);
        }
    }

    private void assertSupportedCurrency(String normalizedCurrency) {
        String siteMetadataCode = toSiteMetadataStringCode(normalizedCurrency);
        if (!currencyOptionRepository.existsByStringCode(siteMetadataCode)) {
            throw ApiException.badRequest(BILLING_CURRENCY_NOT_SUPPORTED, normalizedCurrency);
        }
    }

    private void assertIsoCurrency(String normalizedCurrency) {
        try {
            Currency.getInstance(normalizedCurrency);
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest(BILLING_CURRENCY_INVALID_ISO, normalizedCurrency);
        }
    }
}
