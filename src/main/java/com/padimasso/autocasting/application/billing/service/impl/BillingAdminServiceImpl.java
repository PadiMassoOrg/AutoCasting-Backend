package com.padimasso.autocasting.application.billing.service.impl;

import com.padimasso.autocasting.application.billing.dto.request.BillableItemDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogItemCreateRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogItemUpdateRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.response.*;
import com.padimasso.autocasting.application.billing.mapper.BillingAdminResponseMapper;
import com.padimasso.autocasting.application.billing.model.BillableItemDiscountEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;
import com.padimasso.autocasting.application.billing.model.BillingDiscountEntity;
import com.padimasso.autocasting.application.billing.repository.BillableItemDiscountRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemPriceRepository;
import com.padimasso.autocasting.application.billing.repository.BillableItemRepository;
import com.padimasso.autocasting.application.billing.repository.BillingDiscountRepository;
import com.padimasso.autocasting.application.billing.service.BillingAdminService;
import com.padimasso.autocasting.application.billing.support.BillingAdminRequestValidator;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.application.billing.utils.BillingNormalizationUtils.*;
import static com.padimasso.autocasting.application.billing.utils.BillingPageUtils.toBillingPageResponse;
import static com.padimasso.autocasting.application.billing.utils.BillingPriceSelectionUtils.resolveCurrentPrice;
import static com.padimasso.autocasting.application.billing.utils.general.PaginationUtils.pageRequest;
import static com.padimasso.autocasting.application.billing.utils.general.TextUtils.trimToNull;
import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_DISCOUNT_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_DISCOUNT_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_ITEM_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.BILLING_PRICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BillingAdminServiceImpl implements BillingAdminService {

    private static final Sort ADMIN_BILLING_SORT = Sort.by(Sort.Direction.DESC, "modifiedAt", "id");

    private final BillableItemRepository billableItemRepository;
    private final BillableItemPriceRepository billableItemPriceRepository;
    private final BillingDiscountRepository billingDiscountRepository;
    private final BillableItemDiscountRepository billableItemDiscountRepository;
    private final BillingAdminRequestValidator validator;
    private final BillingAdminResponseMapper responseMapper;

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillingCatalogItemListResponse> listItems(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billableItemRepository.findAll(pageable).map(this::toCatalogListRow);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingCatalogItemDetailResponse getItem(UUID itemId) {
        return toCatalogDetailResponse(requireItem(itemId));
    }

    @Override
    @Transactional
    public BillingCatalogItemDetailResponse createItem(BillingCatalogItemCreateRequest request) {
        BillableItemResponse createdItem = createItemInternal(
            new BillableItemUpsertRequest(
                request.code(),
                request.stringCode(),
                request.description(),
                request.audiences(),
                request.billingType(),
                request.active()
            )
        );

        createPriceInternal(
            new BillableItemPriceUpsertRequest(
                createdItem.id(),
                request.initialPrice().currencyCode(),
                request.initialPrice().amountMinor(),
                request.initialPrice().validFrom(),
                request.initialPrice().validTo(),
                request.initialPrice().active()
            )
        );

        return getItem(createdItem.id());
    }

    @Override
    @Transactional
    public BillingCatalogItemDetailResponse updateItem(UUID itemId, BillingCatalogItemUpdateRequest request) {
        updateItemInternal(
            itemId,
            new BillableItemUpsertRequest(
                request.code(),
                request.stringCode(),
                request.description(),
                request.audiences(),
                request.billingType(),
                request.active()
            )
        );
        return getItem(itemId);
    }

    @Override
    @Transactional
    public void deleteItem(UUID itemId) {
        BillableItemEntity item = requireItem(itemId);
        billableItemPriceRepository.findAllByBillableItem_IdOrderByValidFromDesc(item.getId())
            .forEach(billableItemPriceRepository::softDelete);
        billableItemDiscountRepository.findAllByBillableItem_IdOrderByValidFromDesc(item.getId())
            .forEach(billableItemDiscountRepository::softDelete);
        billableItemRepository.softDelete(item);
    }

    @Override
    @Transactional
    public BillingCatalogPriceResponse createPrice(UUID itemId, BillingCatalogPriceUpsertRequest request) {
        BillableItemPriceResponse createdPrice = createPriceInternal(
            new BillableItemPriceUpsertRequest(
                itemId,
                request.currencyCode(),
                request.amountMinor(),
                request.validFrom(),
                request.validTo(),
                request.active()
            )
        );
        return responseMapper.toCatalogPriceResponse(requirePrice(createdPrice.id()));
    }

    @Override
    @Transactional
    public BillingCatalogPriceResponse updatePrice(UUID priceId, BillingCatalogPriceUpsertRequest request) {
        UUID itemId = requirePrice(priceId).getBillableItem().getId();
        updatePriceInternal(
            priceId,
            new BillableItemPriceUpsertRequest(
                itemId,
                request.currencyCode(),
                request.amountMinor(),
                request.validFrom(),
                request.validTo(),
                request.active()
            )
        );
        return responseMapper.toCatalogPriceResponse(requirePrice(priceId));
    }

    @Override
    @Transactional
    public void deletePrice(UUID priceId) {
        billableItemPriceRepository.softDelete(requirePrice(priceId));
    }

    private BillableItemResponse createItemInternal(BillableItemUpsertRequest request) {
        validator.validateItemCreate(request);

        BillableItemEntity entity = BillableItemEntity.builder()
            .code(normalizeCode(request.code()))
            .stringCode(normalizeStringCode(request.stringCode()))
            .description(trimToNull(request.description()))
            .audiences(new LinkedHashSet<>(request.audiences()))
            .billingType(request.billingType())
            .active(request.active() == null || request.active())
            .build();

        return responseMapper.toItemResponse(billableItemRepository.save(entity));
    }

    private BillableItemResponse updateItemInternal(UUID itemId, BillableItemUpsertRequest request) {
        validator.validateItemUpdate(itemId, request);

        BillableItemEntity entity = requireItem(itemId);

        entity.setCode(normalizeCode(request.code()));
        entity.setStringCode(normalizeStringCode(request.stringCode()));
        entity.setDescription(trimToNull(request.description()));
        entity.setAudiences(new LinkedHashSet<>(request.audiences()));
        entity.setBillingType(request.billingType());
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return responseMapper.toItemResponse(billableItemRepository.save(entity));
    }

    private BillableItemPriceResponse createPriceInternal(BillableItemPriceUpsertRequest request) {
        validator.validatePriceCreate(request);

        BillableItemPriceEntity entity = BillableItemPriceEntity.builder()
            .billableItem(requireItem(request.billableItemId()))
            .currencyCode(normalizeCurrency(request.currencyCode()))
            .amountMinor(request.amountMinor())
            .validFrom(request.validFrom())
            .validTo(request.validTo())
            .active(request.active() == null || request.active())
            .build();

        return responseMapper.toPriceResponse(billableItemPriceRepository.save(entity));
    }

    private BillableItemPriceResponse updatePriceInternal(UUID priceId, BillableItemPriceUpsertRequest request) {
        validator.validatePriceUpdate(priceId, request);

        BillableItemPriceEntity entity = requirePrice(priceId);

        entity.setBillableItem(requireItem(request.billableItemId()));
        entity.setCurrencyCode(normalizeCurrency(request.currencyCode()));
        entity.setAmountMinor(request.amountMinor());
        entity.setValidFrom(request.validFrom());
        entity.setValidTo(request.validTo());
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return responseMapper.toPriceResponse(billableItemPriceRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillingDiscountResponse> listDiscounts(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billingDiscountRepository.findAll(pageable).map(responseMapper::toDiscountResponse);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingDiscountResponse getDiscount(UUID discountId) {
        return responseMapper.toDiscountResponse(requireDiscount(discountId));
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

        return responseMapper.toDiscountResponse(billingDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public BillingDiscountResponse updateDiscount(UUID discountId, BillingDiscountUpsertRequest request) {
        validator.validateDiscountUpdate(discountId, request);

        BillingDiscountEntity entity = requireDiscount(discountId);

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

        return responseMapper.toDiscountResponse(billingDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteDiscount(UUID discountId) {
        billingDiscountRepository.softDelete(requireDiscount(discountId));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPageResponse<BillableItemDiscountResponse> listItemDiscounts(int page, int size) {
        var pageable = pageRequest(page, size, MAX_PAGE_SIZE, ADMIN_BILLING_SORT);
        var result = billableItemDiscountRepository.findAll(pageable).map(responseMapper::toItemDiscountResponse);
        return toBillingPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BillableItemDiscountResponse getItemDiscount(UUID itemDiscountId) {
        return responseMapper.toItemDiscountResponse(requireItemDiscount(itemDiscountId));
    }

    @Override
    @Transactional
    public BillableItemDiscountResponse createItemDiscount(BillableItemDiscountUpsertRequest request) {
        validator.validateItemDiscountCreate(request);

        BillableItemDiscountEntity entity = BillableItemDiscountEntity.builder()
            .billableItem(requireItem(request.billableItemId()))
            .billingDiscount(requireDiscount(request.billingDiscountId()))
            .validFrom(request.validFrom())
            .validTo(request.validTo())
            .priority(request.priority().shortValue())
            .active(request.active() == null || request.active())
            .build();

        return responseMapper.toItemDiscountResponse(billableItemDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public BillableItemDiscountResponse updateItemDiscount(UUID itemDiscountId, BillableItemDiscountUpsertRequest request) {
        validator.validateItemDiscountUpdate(itemDiscountId, request);

        BillableItemDiscountEntity entity = requireItemDiscount(itemDiscountId);

        entity.setBillableItem(requireItem(request.billableItemId()));
        entity.setBillingDiscount(requireDiscount(request.billingDiscountId()));
        entity.setValidFrom(request.validFrom());
        entity.setValidTo(request.validTo());
        entity.setPriority(request.priority().shortValue());
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return responseMapper.toItemDiscountResponse(billableItemDiscountRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteItemDiscount(UUID itemDiscountId) {
        billableItemDiscountRepository.softDelete(requireItemDiscount(itemDiscountId));
    }

    private BillingCatalogItemListResponse toCatalogListRow(BillableItemEntity item) {
        List<BillableItemPriceEntity> prices = billableItemPriceRepository.findAllByBillableItem_IdOrderByValidFromDesc(item.getId());
        return responseMapper.toCatalogItemListResponse(item, resolveCurrentPrice(prices, OffsetDateTime.now()));
    }

    private BillingCatalogItemDetailResponse toCatalogDetailResponse(BillableItemEntity item) {
        List<BillingCatalogPriceResponse> prices = billableItemPriceRepository.findAllByBillableItem_IdOrderByValidFromDesc(item.getId())
            .stream()
            .map(responseMapper::toCatalogPriceResponse)
            .toList();

        List<BillableItemDiscountResponse> itemDiscounts = billableItemDiscountRepository.findAllByBillableItem_IdOrderByValidFromDesc(item.getId())
            .stream()
            .map(responseMapper::toItemDiscountResponse)
            .toList();

        return responseMapper.toCatalogItemDetailResponse(
            item,
            prices,
            itemDiscounts
        );
    }

    private BillableItemEntity requireItem(UUID id) {
        return billableItemRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound(BILLING_ITEM_NOT_FOUND));
    }

    private BillableItemPriceEntity requirePrice(UUID id) {
        return billableItemPriceRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound(BILLING_PRICE_NOT_FOUND));
    }

    private BillingDiscountEntity requireDiscount(UUID id) {
        return billingDiscountRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound(BILLING_DISCOUNT_NOT_FOUND));
    }

    private BillableItemDiscountEntity requireItemDiscount(UUID id) {
        return billableItemDiscountRepository.findById(id)
            .orElseThrow(() -> ApiException.notFound(BILLING_ITEM_DISCOUNT_NOT_FOUND));
    }

}
