package com.padimasso.autocasting.application.proposal.repository;

import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProposalRepository extends SoftDeleteRepository<ProposalEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"type"})
    Page<ProposalEntity> findAll(@Nullable Specification<ProposalEntity> spec, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from ProposalEntity p
        where p.id = :id
          and p.deleted = false
        """)
    Optional<ProposalEntity> findByIdForUpdate(@Param("id") UUID id);

    @Query(value = """
        select distinct pa.proposal_id
          from proposal_attachments pa
         where pa.proposal_id in (:proposalIds)
        """, nativeQuery = true)
    List<UUID> findAttachedProposalIds(@Param("proposalIds") Collection<UUID> proposalIds);
}
