package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "casting_modality_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE casting_modality_option SET deleted = true WHERE id = ?")
public class CastingModalityOptionEntity extends SiteMetadataBase {
}
