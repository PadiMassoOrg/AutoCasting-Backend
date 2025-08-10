package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "diet_option")
@SQLDelete(sql = "UPDATE diet_option SET deleted = true WHERE id = ?")
public class DietOptionEntity extends SiteMetadataBase{
}
