package com.padimasso.autocasting.application.employer.repository;

import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployerProfileRepository extends SoftDeleteRepository<EmployerProfileEntity, UUID>, JpaSpecificationExecutor<EmployerProfileEntity> {

    Optional<EmployerProfileEntity> findByUserId(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select e
        from EmployerProfileEntity e
        where e.id = :id
          and e.deleted = false
        """)
    Optional<EmployerProfileEntity> findByIdForUpdate(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"user", "plan", "basicInfo", "basicInfo.companyType"})
    @Query("""
        select e
        from EmployerProfileEntity e
        where e.user.id = :userId
        """)
    Optional<EmployerProfileEntity> findEmployerProfileForAdminByUserId(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = {"user", "basicInfo"})
    @Query("""
        select distinct e
        from EmployerProfileEntity e
        where e.user.id in :userIds
        """)
    List<EmployerProfileEntity> findAllByUserIdInForAdmin(@Param("userIds") List<UUID> userIds);

}
