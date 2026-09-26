package com.padimasso.autocasting.application.proposal.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalRequirementResponse;
import com.padimasso.autocasting.application.proposal.dto.response.PublicProposalResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.service.ProposalService;
import com.padimasso.autocasting.application.proposal.service.internal.ProposalClaimService;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandler;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandlerRegistry;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_LINK_INVALID;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProposalTypeHandlerRegistry proposalTypeHandlerRegistry;
    private final ProposalClaimService proposalClaimService;
    private final AuthContext authContext;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PublicProposalResponse getByToken(String token) {
        ProposalEntity proposal = findPendingOrGone(token);
        if (proposal.getFirstOpenedAt() == null) {
            proposal.setFirstOpenedAt(LocalDateTime.now());
            proposalRepository.save(proposal);
        }

        ProposalTypeHandler handler = proposalTypeHandlerRegistry.forProposal(proposal);
        return new PublicProposalResponse(
            proposal.getType().getStringCode(),
            ProposalRequirementResponse.of(handler.requirement()),
            handler.buildPreview(proposal)
        );
    }

    @Override
    @Transactional
    public void attach(String token) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProposalEntity proposal = findPendingOrGone(token);

        proposalRepository.attachUser(proposal.getId(), user.getId());
        user.setActiveMode(proposalTypeHandlerRegistry.forProposal(proposal).requirement().requiredMode());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public ProposalClaimResult claim(String token) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProposalEntity proposal = proposalRepository.findByTokenAndDeletedFalse(token).orElseThrow(ProposalServiceImpl::gone);
        ProposalClaimResult result = proposalClaimService.claimIfPending(proposal.getId(), user)
            .orElseThrow(ProposalServiceImpl::gone);

        user.setActiveMode(proposalTypeHandlerRegistry.forProposal(proposal).requirement().requiredMode());
        userRepository.save(user);
        return result;
    }

    @Override
    @Transactional
    public ProposalClaimResult getClaimResult(String token) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProposalEntity proposal = proposalRepository.findByTokenAndDeletedFalse(token)
            .filter(found -> found.getStatus() == ProposalStatus.CLAIMED)
            .filter(found -> found.getClaimedByUser() != null && found.getClaimedByUser().getId().equals(user.getId()))
            .orElseThrow(ProposalServiceImpl::gone);
        return proposalTypeHandlerRegistry.forProposal(proposal).describeClaim(proposal);
    }

    private ProposalEntity findPendingOrGone(String token) {
        return proposalRepository.findByTokenAndDeletedFalse(token)
            .filter(proposal -> proposal.getStatus() == ProposalStatus.PENDING)
            .orElseThrow(ProposalServiceImpl::gone);
    }

    private static ApiException gone() {
        return new ApiException(HttpStatus.GONE, PROPOSALS_LINK_INVALID);
    }
}
