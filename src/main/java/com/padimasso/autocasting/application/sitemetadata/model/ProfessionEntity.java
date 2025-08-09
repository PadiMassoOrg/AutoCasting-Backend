package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "professions")
@SQLDelete(sql = "UPDATE professions SET deleted = true WHERE id = ?")
public class ProfessionEntity extends SiteMetadataBase {
}
