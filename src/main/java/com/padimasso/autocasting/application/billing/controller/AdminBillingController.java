package com.padimasso.autocasting.application.billing.controller;

import com.padimasso.autocasting.application.billing.dto.request.BillableItemDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogItemCreateRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogItemUpdateRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingCatalogPriceUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.request.BillingDiscountUpsertRequest;
import com.padimasso.autocasting.application.billing.dto.response.*;
import com.padimasso.autocasting.application.billing.service.BillingAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Billing", description = "Administrative CRUD endpoints for billing catalog, prices and discounts.")
@SuppressWarnings("unused")
public class AdminBillingController {

    private final BillingAdminService billingAdminService;

    @Operation(summary = "List billable items", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(ADMIN_BILLABLE_ITEMS_API_URL)
    public BillingPageResponse<BillingCatalogItemListResponse> listItems(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Locale tag for display fields. Example: es-AR, en-US.") @RequestParam(required = false) String locale
    ) {
        return billingAdminService.listItems(page, size, locale);
    }

    @Operation(summary = "Get billable item by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(ADMIN_BILLABLE_ITEM_API_URL)
    public BillingCatalogItemDetailResponse getItem(
        @PathVariable UUID itemId,
        @Parameter(description = "Locale tag for display fields. Example: es-AR, en-US.") @RequestParam(required = false) String locale
    ) {
        return billingAdminService.getItem(itemId, locale);
    }

    @Operation(summary = "Create billable item with initial price", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(ADMIN_BILLABLE_ITEMS_API_URL)
    public ResponseEntity<BillingCatalogItemDetailResponse> createItem(@Valid @RequestBody BillingCatalogItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingAdminService.createItem(request));
    }

    @Operation(summary = "Update billable item", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(ADMIN_BILLABLE_ITEM_API_URL)
    public BillingCatalogItemDetailResponse updateItem(
        @PathVariable UUID itemId,
        @Valid @RequestBody BillingCatalogItemUpdateRequest request
    ) {
        return billingAdminService.updateItem(itemId, request);
    }

    @Operation(summary = "Delete billable item", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(ADMIN_BILLABLE_ITEM_API_URL)
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        billingAdminService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create item price", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(ADMIN_BILLABLE_ITEM_PRICES_BY_ITEM_API_URL)
    public ResponseEntity<BillingCatalogPriceResponse> createPrice(
        @PathVariable UUID itemId,
        @Valid @RequestBody BillingCatalogPriceUpsertRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingAdminService.createPrice(itemId, request));
    }

    @Operation(summary = "Update item price", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(ADMIN_BILLABLE_ITEM_PRICE_API_URL)
    public BillingCatalogPriceResponse updatePrice(
        @PathVariable UUID priceId,
        @Valid @RequestBody BillingCatalogPriceUpsertRequest request
    ) {
        return billingAdminService.updatePrice(priceId, request);
    }

    @Operation(summary = "Delete item price", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(ADMIN_BILLABLE_ITEM_PRICE_API_URL)
    public ResponseEntity<Void> deletePrice(@PathVariable UUID priceId) {
        billingAdminService.deletePrice(priceId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List discounts", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(ADMIN_BILLING_DISCOUNTS_API_URL)
    public BillingPageResponse<BillingDiscountResponse> listDiscounts(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size
    ) {
        return billingAdminService.listDiscounts(page, size);
    }

    @Operation(summary = "Get discount by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(ADMIN_BILLING_DISCOUNT_API_URL)
    public BillingDiscountResponse getDiscount(@PathVariable UUID discountId) {
        return billingAdminService.getDiscount(discountId);
    }

    @Operation(summary = "Create discount", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(ADMIN_BILLING_DISCOUNTS_API_URL)
    public ResponseEntity<BillingDiscountResponse> createDiscount(@Valid @RequestBody BillingDiscountUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingAdminService.createDiscount(request));
    }

    @Operation(summary = "Update discount", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(ADMIN_BILLING_DISCOUNT_API_URL)
    public BillingDiscountResponse updateDiscount(
        @PathVariable UUID discountId,
        @Valid @RequestBody BillingDiscountUpsertRequest request
    ) {
        return billingAdminService.updateDiscount(discountId, request);
    }

    @Operation(summary = "Delete discount", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(ADMIN_BILLING_DISCOUNT_API_URL)
    public ResponseEntity<Void> deleteDiscount(@PathVariable UUID discountId) {
        billingAdminService.deleteDiscount(discountId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List billable-item discounts", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(ADMIN_BILLABLE_ITEM_DISCOUNTS_API_URL)
    public BillingPageResponse<BillableItemDiscountResponse> listItemDiscounts(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size
    ) {
        return billingAdminService.listItemDiscounts(page, size);
    }

    @Operation(summary = "Get billable-item discount by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(ADMIN_BILLABLE_ITEM_DISCOUNT_API_URL)
    public BillableItemDiscountResponse getItemDiscount(@PathVariable UUID itemDiscountId) {
        return billingAdminService.getItemDiscount(itemDiscountId);
    }

    @Operation(summary = "Create billable-item discount", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(ADMIN_BILLABLE_ITEM_DISCOUNTS_API_URL)
    public ResponseEntity<BillableItemDiscountResponse> createItemDiscount(@Valid @RequestBody BillableItemDiscountUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingAdminService.createItemDiscount(request));
    }

    @Operation(summary = "Update billable-item discount", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(ADMIN_BILLABLE_ITEM_DISCOUNT_API_URL)
    public BillableItemDiscountResponse updateItemDiscount(
        @PathVariable UUID itemDiscountId,
        @Valid @RequestBody BillableItemDiscountUpsertRequest request
    ) {
        return billingAdminService.updateItemDiscount(itemDiscountId, request);
    }

    @Operation(summary = "Delete billable-item discount", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(ADMIN_BILLABLE_ITEM_DISCOUNT_API_URL)
    public ResponseEntity<Void> deleteItemDiscount(@PathVariable UUID itemDiscountId) {
        billingAdminService.deleteItemDiscount(itemDiscountId);
        return ResponseEntity.noContent().build();
    }
}
