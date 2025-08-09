package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "skills")
@SQLDelete(sql = "UPDATE skills SET deleted = true WHERE id = ?")
public class SkillEntity extends SiteMetadataBase {
}
