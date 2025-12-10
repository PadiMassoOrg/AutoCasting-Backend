package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "casting_section_status_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE casting_section_status_option SET deleted = true WHERE id = ?")
public class CastingSectionStatusOptionEntity extends SiteMetadataBase {
}
