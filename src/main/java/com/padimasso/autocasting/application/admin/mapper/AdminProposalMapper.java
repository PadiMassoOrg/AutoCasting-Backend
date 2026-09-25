package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalProgress;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminProposalMapper {

    public AdminProposalRowResponse toRowResponse(ProposalEntity proposal, boolean hasAttachments) {
        return new AdminProposalRowResponse(
            proposal.getId(),
            TalentProfileMapper.mapToSiteMetadataObject(proposal.getType()),
            proposal.getToken(),
            ProposalProgress.of(proposal.getFirstOpenedAt(), hasAttachments),
            proposal.getContactName(),
            proposal.getContactEmail(),
            proposal.getContactWhatsapp(),
            proposal.getModifiedAt()
        );
    }

    public PageResponse<AdminProposalRowResponse> toPageResponse(List<AdminProposalRowResponse> items, Page<?> result) {
        return new PageResponse<>(
            items,
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
        );
    }
}
