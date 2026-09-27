package com.padimasso.autocasting.application.admin.repository;

import com.padimasso.autocasting.application.admin.model.AdminUserActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminUserActivityRepository extends JpaRepository<AdminUserActivityEntity, UUID> {
}
