package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "pay_rate_type_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE pay_rate_type_option SET deleted = true WHERE id = ?")
public class PayRateTypeOptionEntity extends SiteMetadataBase {
}
