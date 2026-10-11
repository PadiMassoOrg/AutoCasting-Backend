package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "entry_access_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE entry_access_option SET deleted = true WHERE id = ?")
public class EntryAccessOptionEntity extends SiteMetadataBase {
}
