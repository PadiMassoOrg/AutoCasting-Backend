package com.padimasso.autocasting.application.billing.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "billable_item_discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE billable_item_discount SET deleted = true WHERE id = ?")
public class BillableItemDiscountEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billable_item_id", nullable = false)
    private BillableItemEntity billableItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_discount_id", nullable = false)
    private BillingDiscountEntity billingDiscount;

    @Column(nullable = false)
    private OffsetDateTime validFrom;

    @Column
    private OffsetDateTime validTo;

    @Column(nullable = false)
    private short priority;

    @Column(nullable = false)
    private boolean active;
}
