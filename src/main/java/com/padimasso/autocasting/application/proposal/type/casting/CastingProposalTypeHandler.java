package com.padimasso.autocasting.application.proposal.type.casting;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_TYPE_CASTING;

@Component
@RequiredArgsConstructor
public class CastingProposalTypeHandler implements ProposalTypeHandler {

    private final CastingRepository castingRepository;
    private final CastingMediaCleanupService castingMediaCleanupService;

    @Override
    public String typeCode() {
        return PROPOSAL_TYPE_CASTING;
    }

    @Override
    public void discard(ProposalEntity proposal) {
        CastingEntity casting = proposal.getCasting();
        if (casting == null || casting.isDeleted()) return;

        castingRepository.softDelete(casting);
        if (casting.getEmployerProfile() != null) {
            castingMediaCleanupService.deleteCastingFolder(casting.getEmployerProfile().getId(), casting.getId());
        }
    }
}
