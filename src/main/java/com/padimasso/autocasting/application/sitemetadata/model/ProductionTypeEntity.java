package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "production_type")
@SQLDelete(sql = "UPDATE production_type SET deleted = true WHERE id = ?")
public class ProductionTypeEntity extends SiteMetadataBase{
}
