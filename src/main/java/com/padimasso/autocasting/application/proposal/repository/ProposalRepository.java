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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProposalRepository extends SoftDeleteRepository<ProposalEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = {"type", "casting", "claimedByUser"})
    Page<ProposalEntity> findAll(@Nullable Specification<ProposalEntity> spec, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from ProposalEntity p
        where p.id = :id
          and p.deleted = false
        """)
    Optional<ProposalEntity> findByIdForUpdate(@Param("id") UUID id);

    Optional<ProposalEntity> findByTokenAndDeletedFalse(String token);

    @Modifying(flushAutomatically = true)
    @Query(value = """
        insert into proposal_attachments (proposal_id, user_id, attached_at)
        values (:proposalId, :userId, now())
        on conflict do nothing
        """, nativeQuery = true)
    void attachUser(@Param("proposalId") UUID proposalId, @Param("userId") UUID userId);

    @Query(value = """
        select pa.proposal_id
          from proposal_attachments pa
          join proposals p on p.id = pa.proposal_id
         where pa.user_id = :userId
           and p.status = 'PENDING'
           and p.deleted = false
         order by pa.attached_at
        """, nativeQuery = true)
    List<UUID> findPendingProposalIdsAttachedToUser(@Param("userId") UUID userId);

    @Query(value = """
        select distinct pa.proposal_id
          from proposal_attachments pa
         where pa.proposal_id in (:proposalIds)
        """, nativeQuery = true)
    List<UUID> findAttachedProposalIds(@Param("proposalIds") Collection<UUID> proposalIds);
}
