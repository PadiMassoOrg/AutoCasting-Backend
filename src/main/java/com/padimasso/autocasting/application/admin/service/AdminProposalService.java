package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface AdminProposalService {
    PageResponse<AdminProposalRowResponse> listPendingProposals(int page, int size, String q, List<UUID> typeIds);
}
