package com.padimasso.autocasting.application.billing.mapper;

import com.padimasso.autocasting.application.billing.dto.response.BillableItemDiscountResponse;
import com.padimasso.autocasting.application.billing.dto.response.BillableItemPriceResponse;
import com.padimasso.autocasting.application.billing.dto.response.BillableItemResponse;
import com.padimasso.autocasting.application.billing.dto.response.BillingCatalogItemDetailResponse;
import com.padimasso.autocasting.application.billing.dto.response.BillingCatalogItemListResponse;
import com.padimasso.autocasting.application.billing.dto.response.BillingCatalogPriceResponse;
import com.padimasso.autocasting.application.billing.dto.response.BillingDiscountResponse;
import com.padimasso.autocasting.application.billing.model.BillableItemDiscountEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemEntity;
import com.padimasso.autocasting.application.billing.model.BillableItemPriceEntity;
import com.padimasso.autocasting.application.billing.model.BillingDiscountEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class BillingAdminResponseMapper {

    public BillableItemResponse toItemResponse(BillableItemEntity entity) {
        return new BillableItemResponse(
            entity.getId(),
            entity.getCode(),
            entity.getStringCode(),
            entity.getDescription(),
            entity.getAudiences(),
            entity.getBillingType(),
            entity.isActive(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    public BillableItemPriceResponse toPriceResponse(BillableItemPriceEntity entity) {
        return new BillableItemPriceResponse(
            entity.getId(),
            entity.getBillableItem().getId(),
            entity.getCurrencyCode(),
            entity.getAmountMinor(),
            entity.getValidFrom(),
            entity.getValidTo(),
            entity.isActive(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    public BillingDiscountResponse toDiscountResponse(BillingDiscountEntity entity) {
        return new BillingDiscountResponse(
            entity.getId(),
            entity.getCode(),
            entity.getStringCode(),
            entity.getDescription(),
            entity.getDiscountType(),
            entity.getPercentageBps(),
            entity.getAmountMinor(),
            entity.getCurrencyCode(),
            entity.isStackable(),
            entity.isActive(),
            entity.getStartsAt(),
            entity.getEndsAt(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    public BillableItemDiscountResponse toItemDiscountResponse(BillableItemDiscountEntity entity) {
        return new BillableItemDiscountResponse(
            entity.getId(),
            entity.getBillableItem().getId(),
            entity.getBillingDiscount().getId(),
            entity.getValidFrom(),
            entity.getValidTo(),
            entity.getPriority(),
            entity.isActive(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getModifiedAt(),
            entity.getModifiedBy()
        );
    }

    public BillingCatalogPriceResponse toCatalogPriceResponse(BillableItemPriceEntity entity) {
        return new BillingCatalogPriceResponse(
            entity.getId(),
            entity.getCurrencyCode(),
            entity.getAmountMinor(),
            entity.getValidFrom(),
            entity.getValidTo(),
            entity.isActive()
        );
    }

    public BillingCatalogItemListResponse toCatalogItemListResponse(
        BillableItemEntity item,
        Optional<BillableItemPriceEntity> currentPrice
    ) {
        String currentCurrencyCode = currentPrice.map(BillableItemPriceEntity::getCurrencyCode).orElse(null);
        Long currentAmountMinor = currentPrice.map(BillableItemPriceEntity::getAmountMinor).orElse(null);
        OffsetDateTime currentPriceValidFrom = currentPrice.map(BillableItemPriceEntity::getValidFrom).orElse(null);
        OffsetDateTime currentPriceValidTo = currentPrice.map(BillableItemPriceEntity::getValidTo).orElse(null);

        return new BillingCatalogItemListResponse(
            item.getId(),
            item.getCode(),
            item.getStringCode(),
            item.getAudiences(),
            item.getBillingType(),
            item.isActive(),
            currentCurrencyCode,
            currentAmountMinor,
            currentPriceValidFrom,
            currentPriceValidTo,
            item.getModifiedAt()
        );
    }

    public BillingCatalogItemDetailResponse toCatalogItemDetailResponse(
        BillableItemEntity item,
        List<BillingCatalogPriceResponse> prices,
        List<BillableItemDiscountResponse> itemDiscounts
    ) {
        return new BillingCatalogItemDetailResponse(
            item.getId(),
            item.getCode(),
            item.getStringCode(),
            item.getDescription(),
            item.getAudiences(),
            item.getBillingType(),
            item.isActive(),
            item.isDeleted(),
            item.getCreatedAt(),
            item.getCreatedBy(),
            item.getModifiedAt(),
            item.getModifiedBy(),
            prices,
            itemDiscounts
        );
    }
}
