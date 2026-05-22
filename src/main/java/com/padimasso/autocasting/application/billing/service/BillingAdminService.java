package com.padimasso.autocasting.application.billing.service;

import com.padimasso.autocasting.application.billing.dto.request.BillableItemDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogItemCreateRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogItemUpdateRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.response.*;

import java.util.UUID;

public interface BillingAdminService {
    BillingPageResponse<BillingCatalogItemListResponse> listItems(int page, int size);

    BillingCatalogItemDetailResponse getItem(UUID itemId);

    BillingCatalogItemDetailResponse createItem(BillingCatalogItemCreateRequest request);

    BillingCatalogItemDetailResponse updateItem(UUID itemId, BillingCatalogItemUpdateRequest request);

    void deleteItem(UUID itemId);

    BillingCatalogPriceResponse createPrice(UUID itemId, BillingCatalogPriceUpsertRequest request);

    BillingCatalogPriceResponse updatePrice(UUID priceId, BillingCatalogPriceUpsertRequest request);

    void deletePrice(UUID priceId);

    BillingPageResponse<BillingDiscountResponse> listDiscounts(int page, int size);

    BillingDiscountResponse getDiscount(UUID discountId);

    BillingDiscountResponse createDiscount(BillingDiscountUpsertRequest request);

    BillingDiscountResponse updateDiscount(UUID discountId, BillingDiscountUpsertRequest request);

    void deleteDiscount(UUID discountId);

    BillingPageResponse<BillableItemDiscountResponse> listItemDiscounts(int page, int size);

    BillableItemDiscountResponse getItemDiscount(UUID itemDiscountId);

    BillableItemDiscountResponse createItemDiscount(BillableItemDiscountUpsertRequest request);

    BillableItemDiscountResponse updateItemDiscount(UUID itemDiscountId, BillableItemDiscountUpsertRequest request);

    void deleteItemDiscount(UUID itemDiscountId);
}
