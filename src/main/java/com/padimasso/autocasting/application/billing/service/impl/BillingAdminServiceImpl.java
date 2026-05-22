package com.padimasso.autocasting.application.billing.service.impl;

import com.padimasso.autocasting.application.billing.dto.request.BillableItemDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.response.*;
import com.padimasso.autocasting.application.billing.model.BillableItemDiscountEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;
import com.padimasso.autocasting.application.billing.model.BillingDiscountEntity;
import com.padimasso.autocasting.application.billing.repository.BillableItemDiscountRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemPriceRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemRepository;
import com.padimasso.autocasting.application.billing.repository.BillingDiscountRepository;
import com.padimasso.autocasting.application.billing.service.BillingAdminService;
import com.padimasso.autocasting.application.billing.support.BillingAdminEntityResolver;
import com.padimasso.autocasting.application.billing.support.BillingAdminRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.UUID;

import static com.padimasso.autocasting.application.billing.utils.BillingNormalizationUtils.*;
import static com.padimasso.autocasting.application.billing.utils.BillingPageUtils.toBillingPageResponse;
import static com.padimasso.autocasting.application.billing.utils.general.PaginationUtils.pageRequest;
import static com.padimasso.autocasting.application.billing.utils.general.TextUtils.trimToNull;
import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class BillingAdminServiceImpl implements BillingAdminService {

    private static final Sort ADMIN_BILLING_SORT = Sort.by(Sort.Direction.DESC, "modifiedAt", "id");

    private final BillableItemRepository billableItemRepository;
    private final BillableItemPriceRepository billableItemPriceRepository;
    private final BillingDiscountRepository billingDiscountRepository;
    private final BillableItemDiscountRepository billableItemDiscountRepository;
    private final BillingAdminEntityResolver resolver;
    private final BillingAdminRequestValidator validator;

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillableItemResponse> listItems(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billableItemRepository.findAll(pageable).map(BillableItemResponse::from);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillableItemResponse getItem(UUID itemId) {
        return BillableItemResponse.from(resolver.requireItem(itemId));
    }

    @Override
    @Transactional
    public BillableItemResponse createItem(BillableItemUpsertRequest request) {
        validator.validateItemCreate(request);

        BillableItemEntity entity = BillableItemEntity.builder()
            .code(normalizeCode(request.code()))
            .stringCode(normalizeStringCode(request.stringCode()))
            .description(trimToNull(request.description()))
            .audiences(new LinkedHashSet<>(request.audiences()))
            .billingType(request.billingType())
            .active(request.active() == null || request.active())
            .build();

        return BillableItemResponse.from(billableItemRepository.save(entity));
    }

    @Override
    @Transactional
    public BillableItemResponse updateItem(UUID itemId, BillableItemUpsertRequest request) {
        validator.validateItemUpdate(itemId, request);

        BillableItemEntity entity = resolver.requireItem(itemId);

        entity.setCode(normalizeCode(request.code()));
        entity.setStringCode(normalizeStringCode(request.stringCode()));
        entity.setDescription(trimToNull(request.description()));
        entity.setAudiences(new LinkedHashSet<>(request.audiences()));
        entity.setBillingType(request.billingType());
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return BillableItemResponse.from(billableItemRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteItem(UUID itemId) {
        billableItemRepository.softDelete(resolver.requireItem(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillableItemPriceResponse> listPrices(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billableItemPriceRepository.findAll(pageable).map(BillableItemPriceResponse::from);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillableItemPriceResponse getPrice(UUID priceId) {
        return BillableItemPriceResponse.from(resolver.requirePrice(priceId));
    }

    @Override
    @Transactional
    public BillableItemPriceResponse createPrice(BillableItemPriceUpsertRequest request) {
        validator.validatePriceCreate(request);

        BillableItemPriceEntity entity = BillableItemPriceEntity.builder()
            .billableItem(resolver.requireItem(request.billableItemId()))
            .currencyCode(normalizeCurrency(request.currencyCode()))
            .amountMinor(request.amountMinor())
            .validFrom(request.validFrom())
            .validTo(request.validTo())
            .active(request.active() == null || request.active())
            .build();

        return BillableItemPriceResponse.from(billableItemPriceRepository.save(entity));
    }

    @Override
    @Transactional
    public BillableItemPriceResponse updatePrice(UUID priceId, BillableItemPriceUpsertRequest request) {
        validator.validatePriceUpdate(priceId, request);

        BillableItemPriceEntity entity = resolver.requirePrice(priceId);

        entity.setBillableItem(resolver.requireItem(request.billableItemId()));
        entity.setCurrencyCode(normalizeCurrency(request.currencyCode()));
        entity.setAmountMinor(request.amountMinor());
        entity.setValidFrom(request.validFrom());
        entity.setValidTo(request.validTo());
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return BillableItemPriceResponse.from(billableItemPriceRepository.save(entity));
    }

    @Override
    @Transactional
    public void deletePrice(UUID priceId) {
        billableItemPriceRepository.softDelete(resolver.requirePrice(priceId));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillingDiscountResponse> listDiscounts(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billingDiscountRepository.findAll(pageable).map(BillingDiscountResponse::from);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingDiscountResponse getDiscount(UUID discountId) {
        return BillingDiscountResponse.from(resolver.requireDiscount(discountId));
    }

    @Override
    @Transactional
    public BillingDiscountResponse createDiscount(BillingDiscountUpsertRequest request) {
        validator.validateDiscountCreate(request);

        BillingDiscountEntity entity = BillingDiscountEntity.builder()
            .code(normalizeCode(request.code()))
            .stringCode(normalizeStringCode(request.stringCode()))
            .description(trimToNull(request.description()))
            .discountType(request.discountType())
            .percentageBps(request.percentageBps())
            .amountMinor(request.amountMinor())
            .currencyCode(normalizeNullableCurrency(request.currencyCode()))
            .stackable(request.stackable() != null && request.stackable())
            .active(request.active() == null || request.active())
            .startsAt(request.startsAt())
            .endsAt(request.endsAt())
            .build();

        return BillingDiscountResponse.from(billingDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public BillingDiscountResponse updateDiscount(UUID discountId, BillingDiscountUpsertRequest request) {
        validator.validateDiscountUpdate(discountId, request);

        BillingDiscountEntity entity = resolver.requireDiscount(discountId);

        entity.setCode(normalizeCode(request.code()));
        entity.setStringCode(normalizeStringCode(request.stringCode()));
        entity.setDescription(trimToNull(request.description()));
        entity.setDiscountType(request.discountType());
        entity.setPercentageBps(request.percentageBps());
        entity.setAmountMinor(request.amountMinor());
        entity.setCurrencyCode(normalizeNullableCurrency(request.currencyCode()));
        if (request.stackable() != null) {
            entity.setStackable(request.stackable());
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }
        entity.setStartsAt(request.startsAt());
        entity.setEndsAt(request.endsAt());

        return BillingDiscountResponse.from(billingDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDiscount(UUID discountId) {
        billingDiscountRepository.softDelete(resolver.requireDiscount(discountId));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillableItemDiscountResponse> listItemDiscounts(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billableItemDiscountRepository.findAll(pageable).map(BillableItemDiscountResponse::from);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillableItemDiscountResponse getItemDiscount(UUID itemDiscountId) {
        return BillableItemDiscountResponse.from(resolver.requireItemDiscount(itemDiscountId));
    }

    @Override
    @Transactional
    public BillableItemDiscountResponse createItemDiscount(BillableItemDiscountUpsertRequest request) {
        validator.validateItemDiscountCreate(request);

        BillableItemDiscountEntity entity = BillableItemDiscountEntity.builder()
            .billableItem(resolver.requireItem(request.billableItemId()))
            .billingDiscount(resolver.requireDiscount(request.billingDiscountId()))
            .validFrom(request.validFrom())
            .validTo(request.validTo())
            .priority(request.priority().shortValue())
            .active(request.active() == null || request.active())
            .build();

        return BillableItemDiscountResponse.from(billableItemDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public BillableItemDiscountResponse updateItemDiscount(UUID itemDiscountId, BillableItemDiscountUpsertRequest request) {
        validator.validateItemDiscountUpdate(itemDiscountId, request);

        BillableItemDiscountEntity entity = resolver.requireItemDiscount(itemDiscountId);

        entity.setBillableItem(resolver.requireItem(request.billableItemId()));
        entity.setBillingDiscount(resolver.requireDiscount(request.billingDiscountId()));
        entity.setValidFrom(request.validFrom());
        entity.setValidTo(request.validTo());
        entity.setPriority(request.priority().shortValue());
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return BillableItemDiscountResponse.from(billableItemDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteItemDiscount(UUID itemDiscountId) {
        billableItemDiscountRepository.softDelete(resolver.requireItemDiscount(itemDiscountId));
    }

}
