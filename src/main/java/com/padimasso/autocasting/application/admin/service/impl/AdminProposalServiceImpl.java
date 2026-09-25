package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProposalMapper;
import com.padimasso.autocasting.application.admin.repository.specification.AdminProposalSpecs;
import com.padimasso.autocasting.application.admin.service.AdminProposalService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class AdminProposalServiceImpl implements AdminProposalService {

    private final ProposalRepository proposalRepository;
    private final AdminProposalMapper adminProposalMapper;

    @Override
    public PageResponse<AdminProposalRowResponse> listPendingProposals(int page, int size, String q, List<UUID> typeIds) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        var spec = AdminProposalSpecs.pendingOfTypes(typeIds).and(AdminProposalSpecs.fromSearchText(q));
        var result = proposalRepository.findAll(spec, pageable);

        Set<UUID> attachedIds = findAttachedIds(result.getContent().stream().map(ProposalEntity::getId).toList());

        var items = result.getContent().stream()
            .map(proposal -> adminProposalMapper.toRowResponse(proposal, attachedIds.contains(proposal.getId())))
            .toList();

        return adminProposalMapper.toPageResponse(items, result);
    }

    private Set<UUID> findAttachedIds(List<UUID> proposalIds) {
        if (proposalIds.isEmpty()) return Set.of();
        return new HashSet<>(proposalRepository.findAttachedProposalIds(proposalIds));
    }
}
