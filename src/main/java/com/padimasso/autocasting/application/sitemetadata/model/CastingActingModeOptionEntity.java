package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "casting_acting_mode_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE casting_acting_mode_option SET deleted = true WHERE id = ?")
public class CastingActingModeOptionEntity extends SiteMetadataBase {
}
