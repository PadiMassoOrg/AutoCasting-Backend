package com.padimasso.autocasting.application.proposal.service.internal;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandler;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandlerRegistry;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_REQUIREMENT_NOT_MET;

@Component
@RequiredArgsConstructor
public class ProposalClaimService {

    private final ProposalRepository proposalRepository;
    private final ProposalTypeHandlerRegistry proposalTypeHandlerRegistry;

    public Optional<ProposalClaimResult> claimIfPending(UUID proposalId, UserEntity user) {
        ProposalEntity proposal = lockPending(proposalId);
        if (proposal == null) return Optional.empty();

        ProposalTypeHandler handler = proposalTypeHandlerRegistry.forProposal(proposal);
        if (!handler.requirement().isSatisfiedBy(user)) {
            throw ApiException.conflict(PROPOSALS_REQUIREMENT_NOT_MET);
        }
        return Optional.of(claim(proposal, handler, user));
    }

    public void claimAttachedProposalsSatisfiedBy(UserEntity user) {
        for (UUID proposalId : proposalRepository.findPendingProposalIdsAttachedToUser(user.getId())) {
            ProposalEntity proposal = lockPending(proposalId);
            if (proposal == null) continue;

            ProposalTypeHandler handler = proposalTypeHandlerRegistry.forProposal(proposal);
            if (handler.requirement().isSatisfiedBy(user)) {
                claim(proposal, handler, user);
            }
        }
    }

    private ProposalEntity lockPending(UUID proposalId) {
        return proposalRepository.findByIdForUpdate(proposalId)
            .filter(proposal -> proposal.getStatus() == ProposalStatus.PENDING)
            .orElse(null);
    }

    private ProposalClaimResult claim(ProposalEntity proposal, ProposalTypeHandler handler, UserEntity user) {
        handler.associate(proposal, user);
        proposal.setStatus(ProposalStatus.CLAIMED);
        proposal.setClaimedByUser(user);
        proposal.setClaimedAt(LocalDateTime.now());
        proposalRepository.save(proposal);
        return handler.describeClaim(proposal);
    }
}
