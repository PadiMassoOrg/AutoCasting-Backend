package com.padimasso.autocasting.application.proposal.controller;

import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.dto.response.PublicProposalResponse;
import com.padimasso.autocasting.application.proposal.service.ProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_API_URL;
import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_ATTACH_API_URL;
import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_CLAIM_API_URL;
import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_CLAIM_RESULT_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Proposals", description = "Public proposal link endpoints.")
public class ProposalController {

    private final ProposalService proposalService;

    @Operation(
        summary = "Open a proposal link",
        description = "Returns the proposal type, the requirement to claim it and a preview of its content. "
            + "Invalid, claimed or revoked links return 410."
    )
    @GetMapping(PROPOSAL_API_URL)
    public PublicProposalResponse getProposal(@Parameter(description = "Link token.") @PathVariable String token) {
        return proposalService.getByToken(token);
    }

    @Operation(
        summary = "Attach the current user to a proposal",
        description = "For a user who doesn't meet the requirement yet: records the attachment and sets the active mode "
            + "the requirement needs. The proposal is claimed automatically once the requirement is met. Idempotent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(PROPOSAL_ATTACH_API_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attach(@Parameter(description = "Link token.") @PathVariable String token) {
        proposalService.attach(token);
    }

    @Operation(
        summary = "Claim a proposal",
        description = "For a user who already meets the requirement: associates the proposal content to their account. "
            + "409 if the requirement isn't met, 410 if the link is no longer valid.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(PROPOSAL_CLAIM_API_URL)
    public ProposalClaimResult claim(@Parameter(description = "Link token.") @PathVariable String token) {
        return proposalService.claim(token);
    }

    @Operation(
        summary = "Get the claim result",
        description = "Returns the result of a claim made by the current user (e.g. after onboarding). 410 otherwise.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(PROPOSAL_CLAIM_RESULT_API_URL)
    public ProposalClaimResult getClaimResult(@Parameter(description = "Link token.") @PathVariable String token) {
        return proposalService.getClaimResult(token);
    }
}
