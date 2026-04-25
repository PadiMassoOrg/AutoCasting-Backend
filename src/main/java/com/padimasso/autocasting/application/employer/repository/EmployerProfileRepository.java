package com.padimasso.autocasting.application.employer.repository;

import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmployerProfileRepository extends SoftDeleteRepository<EmployerProfileEntity, UUID>, JpaSpecificationExecutor<EmployerProfileEntity> {

    Optional<EmployerProfileEntity> findByUserId(UUID id);

    @EntityGraph(attributePaths = {"user", "plan", "basicInfo", "basicInfo.companyType"})
    @Query("""
        select e
        from EmployerProfileEntity e
        where e.user.id = :userId
        """)
    Optional<EmployerProfileEntity> findEmployerProfileForAdminByUserId(@Param("userId") UUID userId);

}
