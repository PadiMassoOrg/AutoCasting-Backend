package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProposalMapper;
import com.padimasso.autocasting.application.admin.repository.specification.AdminProposalSpecs;
import com.padimasso.autocasting.application.admin.service.AdminProposalService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalAssociatedEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalLinkResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.service.internal.ProposalTokenGenerator;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandlerRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_NOT_PENDING;

@Service
@RequiredArgsConstructor
public class AdminProposalServiceImpl implements AdminProposalService {

    private final ProposalRepository proposalRepository;
    private final AdminProposalMapper adminProposalMapper;
    private final ProposalTokenGenerator proposalTokenGenerator;
    private final ProposalTypeHandlerRegistry proposalTypeHandlerRegistry;

    @Override
    public PageResponse<AdminProposalRowResponse> listProposals(
        int page,
        int size,
        String q,
        List<UUID> typeIds,
        List<ProposalStatus> statuses
    ) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "modifiedAt", "id")
        );

        var spec = AdminProposalSpecs.listable(typeIds, statuses).and(AdminProposalSpecs.fromSearchText(q));
        var result = proposalRepository.findAll(spec, pageable);

        Set<UUID> attachedIds = findAttachedIds(result.getContent().stream().map(ProposalEntity::getId).toList());

        var items = result.getContent().stream()
            .map(proposal -> adminProposalMapper.toRowResponse(
                proposal,
                attachedIds.contains(proposal.getId()),
                associatedEntities(proposal)
            ))
            .toList();

        return adminProposalMapper.toPageResponse(items, result);
    }

    @Override
    @Transactional
    public ProposalLinkResponse regenerateLink(UUID proposalId) {
        ProposalEntity proposal = lockPendingProposal(proposalId);
        proposal.setToken(proposalTokenGenerator.generate());
        proposal.setFirstOpenedAt(null);
        return new ProposalLinkResponse(proposalRepository.save(proposal).getToken());
    }

    @Override
    @Transactional
    public void revoke(UUID proposalId) {
        ProposalEntity proposal = lockPendingProposal(proposalId);
        proposal.setStatus(ProposalStatus.REVOKED);
        proposalRepository.save(proposal);
        proposalTypeHandlerRegistry.forProposal(proposal).discard(proposal);
    }

    private ProposalEntity lockPendingProposal(UUID proposalId) {
        ProposalEntity proposal = proposalRepository.findByIdForUpdate(proposalId)
            .orElseThrow(() -> new IllegalArgumentException(PROPOSALS_NOT_FOUND));
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new IllegalStateException(PROPOSALS_NOT_PENDING);
        }
        return proposal;
    }

    private List<ProposalAssociatedEntity> associatedEntities(ProposalEntity proposal) {
        if (proposal.getStatus() != ProposalStatus.CLAIMED) return List.of();
        return proposalTypeHandlerRegistry.forProposal(proposal).associatedEntities(proposal);
    }

    private Set<UUID> findAttachedIds(List<UUID> proposalIds) {
        if (proposalIds.isEmpty()) return Set.of();
        return new HashSet<>(proposalRepository.findAttachedProposalIds(proposalIds));
    }
}
