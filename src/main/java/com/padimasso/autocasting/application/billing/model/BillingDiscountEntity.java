package com.padimasso.autocasting.application.billing.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "billing_discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE billing_discount SET deleted = true WHERE id = ?")
public class BillingDiscountEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String stringCode;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BillingDiscountType discountType;

    @Column
    private Integer percentageBps;

    @Column
    private Long amountMinor;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 3, columnDefinition = "char(3)")
    private String currencyCode;

    @Column(nullable = false)
    private boolean stackable;

    @Column(nullable = false)
    private boolean active;

    @Column
    private OffsetDateTime startsAt;

    @Column
    private OffsetDateTime endsAt;
}
