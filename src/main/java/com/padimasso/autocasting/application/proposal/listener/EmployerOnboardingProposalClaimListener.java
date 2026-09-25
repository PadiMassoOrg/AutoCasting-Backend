package com.padimasso.autocasting.application.proposal.listener;

import com.padimasso.autocasting.application.auth.event.EmployerOnboardingCompletedEvent;
import com.padimasso.autocasting.application.proposal.service.internal.ProposalClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployerOnboardingProposalClaimListener {

    private final ProposalClaimService proposalClaimService;

    @EventListener
    public void onEmployerOnboardingCompleted(EmployerOnboardingCompletedEvent event) {
        proposalClaimService.claimAttachedProposalsSatisfiedBy(event.user());
    }
}
