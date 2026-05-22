package com.padimasso.autocasting.application.billing.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "billable_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE billable_item SET deleted = true WHERE id = ?")
public class BillableItemEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String stringCode;

    @Column(columnDefinition = "text")
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "billable_item_audience",
        joinColumns = @JoinColumn(name = "billable_item_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "audience", nullable = false, length = 32)
    @Builder.Default
    private Set<BillingAudience> audiences = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BillingType billingType;

    @Column(nullable = false)
    private boolean active;
}
