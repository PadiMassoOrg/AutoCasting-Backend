package com.padimasso.autocasting.application.proposal.type.casting;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CastingProposalTypeHandlerTest {

    @Mock
    private CastingRepository castingRepository;
    @Mock
    private CastingMediaCleanupService castingMediaCleanupService;
    @InjectMocks
    private CastingProposalTypeHandler handler;

    @Test
    void discard_softDeletesCastingAndCleansItsMediaFolder() {
        UUID ownerId = UUID.randomUUID();
        CastingEntity casting = CastingEntity.builder()
            .id(UUID.randomUUID())
            .employerProfile(EmployerProfileEntity.builder().id(ownerId).build())
            .build();

        handler.discard(ProposalEntity.builder().casting(casting).build());

        verify(castingRepository).softDelete(casting);
        verify(castingMediaCleanupService).deleteCastingFolder(ownerId, casting.getId());
    }

    @Test
    void discard_alreadyDeletedCasting_doesNothing() {
        CastingEntity casting = CastingEntity.builder().id(UUID.randomUUID()).build();
        casting.setDeleted(true);

        handler.discard(ProposalEntity.builder().casting(casting).build());

        verify(castingRepository, never()).softDelete(any());
        verify(castingMediaCleanupService, never()).deleteCastingFolder(any(), any());
    }
}
