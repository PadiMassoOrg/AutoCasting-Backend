package com.padimasso.autocasting.application.billing.service;

import com.padimasso.autocasting.application.billing.dto.request.BillableItemDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillableItemUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.response.*;

import java.util.UUID;

public interface BillingAdminService {
    BillingPageResponse<BillableItemResponse> listItems(int page, int size);

    BillableItemResponse getItem(UUID itemId);

    BillableItemResponse createItem(BillableItemUpsertRequest request);

    BillableItemResponse updateItem(UUID itemId, BillableItemUpsertRequest request);

    void deleteItem(UUID itemId);

    BillingPageResponse<BillableItemPriceResponse> listPrices(int page, int size);

    BillableItemPriceResponse getPrice(UUID priceId);

    BillableItemPriceResponse createPrice(BillableItemPriceUpsertRequest request);

    BillableItemPriceResponse updatePrice(UUID priceId, BillableItemPriceUpsertRequest request);

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
