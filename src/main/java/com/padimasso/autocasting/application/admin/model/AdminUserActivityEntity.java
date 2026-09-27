package com.padimasso.autocasting.application.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "admin_user_activity")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserActivityEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "talent_stage_name")
    private String talentStageName;

    @Column(name = "employer_company_name")
    private String employerCompanyName;

    @Column(name = "last_saved_at")
    private LocalDateTime lastSavedAt;
}
