package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.admin.service.AdminProposalService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_PROPOSALS_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative proposal management endpoints.")
public class AdminProposalController {

    private final AdminProposalService adminProposalService;

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
}
