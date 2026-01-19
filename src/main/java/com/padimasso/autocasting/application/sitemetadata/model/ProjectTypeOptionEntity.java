package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "project_type_option")
@Getter
@Setter
@SQLDelete(sql = "UPDATE project_type_option SET deleted = true WHERE id = ?")
public class ProjectTypeOptionEntity extends SiteMetadataBase {
}
