package com.padimasso.autocasting.application.sitemetadata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "skills")
@Getter
@Setter
@SQLDelete(sql = "UPDATE skills SET deleted = true WHERE id = ?")
public class SkillEntity extends SiteMetadataBase {

    @Column(nullable = false)
    private String category;

}
