package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.admin.service.AdminProposalService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalCreateRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalCreatedResponse;
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
}
