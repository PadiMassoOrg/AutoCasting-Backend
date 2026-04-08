package com.padimasso.autocasting.application.castings.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "casting_role_remuneration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE casting_role_remuneration SET deleted = true WHERE id = ?")
public class CastingRoleRemunerationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "casting_role_id", nullable = false, unique = true)
    private CastingRoleEntity castingRole;

    @Column(nullable = false)
    private boolean isComplete;

    @ManyToOne
    @JoinColumn(name = "pay_rate_type_option_id")
    private PayRateTypeOptionEntity payRateType;

    @ManyToOne
    @JoinColumn(name = "currency_option_id")
    private CurrencyOptionEntity currency;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "text")
    private String notes;

}
