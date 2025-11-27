package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "ethnicity_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE ethnicity_option SET deleted = true WHERE id = ?")
public class EthnicityOptionEntity extends SiteMetadataBase{
}
