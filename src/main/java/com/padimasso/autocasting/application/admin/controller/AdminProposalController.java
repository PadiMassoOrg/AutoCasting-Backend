package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.admin.service.AdminProposalService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalLinkResponse;
import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalCreateRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalCreatedResponse;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalDetailsResponse;
import com.padimasso.autocasting.application.proposal.type.casting.service.CastingProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_PROPOSALS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_PROPOSALS_CASTINGS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_PROPOSAL_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_PROPOSAL_CASTING_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_PROPOSAL_REGENERATE_LINK_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative proposal management endpoints.")
public class AdminProposalController {

    private final AdminProposalService adminProposalService;
    private final CastingProposalService castingProposalService;

    @Operation(
        summary = "List pending proposals (paginated)",
        description = "Returns proposals still waiting to be claimed, with their derived progress (link generated, link opened, account attached).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_PROPOSALS_API_URL)
    public PageResponse<AdminProposalRowResponse> listProposals(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Free text search over contact name, contact email, contact WhatsApp and company name hint.")
        @RequestParam(required = false) String q,
        @Parameter(description = "Proposal type option IDs (sitemetadata proposalTypeOptions). When omitted, pending proposals of every type are returned.")
        @RequestParam(required = false, name = "typeId") List<UUID> typeIds
    ) {
        return adminProposalService.listPendingProposals(page, size, q, typeIds);
    }

    @Operation(
        summary = "Create a Casting Proposal",
        description = "Creates a complete casting (with its roles) owned by the proposals system employer, in DRAFT, "
            + "plus a PENDING proposal with its link token. Incomplete castings or past deadlines are rejected and nothing is saved.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_PROPOSALS_CASTINGS_API_URL)
    @ResponseStatus(HttpStatus.CREATED)
    public CastingProposalCreatedResponse createCastingProposal(@Valid @RequestBody CastingProposalCreateRequest request) {
        return castingProposalService.create(request);
    }

    @Operation(
        summary = "Regenerate a proposal link",
        description = "Issues a new link token for a PENDING proposal; the previous link stops working immediately.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_PROPOSAL_REGENERATE_LINK_API_URL)
    public ProposalLinkResponse regenerateProposalLink(@Parameter(description = "Proposal ID.") @PathVariable UUID proposalId) {
        return adminProposalService.regenerateLink(proposalId);
    }

    @Operation(
        summary = "Delete (revoke) a proposal",
        description = "Marks a PENDING proposal as REVOKED (its link stops working) and discards the content prepared for it.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(ADMIN_PROPOSAL_API_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeProposal(@Parameter(description = "Proposal ID.") @PathVariable UUID proposalId) {
        adminProposalService.revoke(proposalId);
    }

    @Operation(
        summary = "Get a Casting Proposal",
        description = "Returns the proposal (link, progress, internal reference) with its casting and roles.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_PROPOSAL_CASTING_API_URL)
    public CastingProposalDetailsResponse getCastingProposal(@Parameter(description = "Proposal ID.") @PathVariable UUID proposalId) {
        return castingProposalService.getDetails(proposalId);
    }

    @Operation(
        summary = "Update a Casting Proposal",
        description = "Replaces the casting, its roles (upsert by id; omitted roles are removed) and the internal reference "
            + "of a PENDING proposal. Same completeness and deadline rules as creation.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(ADMIN_PROPOSAL_CASTING_API_URL)
    public CastingProposalDetailsResponse updateCastingProposal(
        @Parameter(description = "Proposal ID.") @PathVariable UUID proposalId,
        @Valid @RequestBody CastingProposalCreateRequest request
    ) {
        return castingProposalService.update(proposalId, request);
    }
}
