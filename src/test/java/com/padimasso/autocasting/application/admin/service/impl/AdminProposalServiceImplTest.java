package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProposalMapper;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalProgress;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProposalServiceImplTest {

    @Mock
    private ProposalRepository proposalRepository;

    private AdminProposalServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminProposalServiceImpl(proposalRepository, new AdminProposalMapper());
    }

    private final ProposalTypeOptionEntity castingType = castingType();

    private static ProposalTypeOptionEntity castingType() {
        ProposalTypeOptionEntity type = new ProposalTypeOptionEntity();
        type.setId(UUID.randomUUID());
        type.setStringCode("sitemetadata.proposal_type.casting");
        return type;
    }

    private ProposalEntity proposal(LocalDateTime firstOpenedAt) {
        return ProposalEntity.builder()
            .id(UUID.randomUUID())
            .type(castingType)
            .token("token-" + UUID.randomUUID())
            .firstOpenedAt(firstOpenedAt)
            .build();
    }

    @SuppressWarnings("unchecked")
    private void givenPage(List<ProposalEntity> content) {
        when(proposalRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenAnswer(invocation -> new PageImpl<>(content, invocation.getArgument(1), content.size()));
    }

    @Test
    void listPendingProposals_derivesProgressPerRowFromOneAttachmentsLookup() {
        ProposalEntity generated = proposal(null);
        ProposalEntity opened = proposal(LocalDateTime.now());
        ProposalEntity attached = proposal(LocalDateTime.now());
        givenPage(List.of(generated, opened, attached));
        when(proposalRepository.findAttachedProposalIds(anyCollection())).thenReturn(List.of(attached.getId()));

        PageResponse<AdminProposalRowResponse> response = service.listPendingProposals(0, 20, null, List.of(castingType.getId()));

        assertEquals(
            List.of(ProposalProgress.LINK_GENERATED, ProposalProgress.LINK_OPENED, ProposalProgress.ACCOUNT_ATTACHED),
            response.items().stream().map(AdminProposalRowResponse::progress).toList()
        );
        assertEquals(3, response.totalElements());
        assertEquals("sitemetadata.proposal_type.casting", response.items().getFirst().type().stringCode());
    }

    @Test
    void listPendingProposals_emptyPage_skipsAttachmentsLookup() {
        givenPage(List.of());

        PageResponse<AdminProposalRowResponse> response = service.listPendingProposals(0, 20, null, null);

        assertTrue(response.items().isEmpty());
        verify(proposalRepository, never()).findAttachedProposalIds(anyCollection());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listPendingProposals_clampsPagingAndSortsNewestFirst() {
        givenPage(List.of());

        service.listPendingProposals(-3, 500, null, null);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(proposalRepository).findAll(any(Specification.class), pageable.capture());
        assertEquals(0, pageable.getValue().getPageNumber());
        assertEquals(MAX_PAGE_SIZE, pageable.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "createdAt", "id"), pageable.getValue().getSort());
    }
}
