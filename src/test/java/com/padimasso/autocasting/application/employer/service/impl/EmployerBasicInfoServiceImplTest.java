package com.padimasso.autocasting.application.employer.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerBasicInfoRepository;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.sitemetadata.model.CompanyTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerBasicInfoServiceImplTest {

    @Mock
    private EmployerContext employerContext;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private EmployerBasicInfoRepository employerBasicInfoRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private EmployerProfileMapper employerProfileMapper;

    private EmployerBasicInfoServiceImpl service;

    private UUID employerProfileId;
    private EmployerProfileEntity employerProfile;

    private static final EmployerBasicInfoPatchRequest EMPTY_PATCH = new EmployerBasicInfoPatchRequest(
        JsonNullable.undefined(),
        JsonNullable.undefined(),
        null,
        JsonNullable.undefined(),
        JsonNullable.undefined(),
        JsonNullable.undefined(),
        JsonNullable.undefined(),
        JsonNullable.undefined()
    );

    @BeforeEach
    void setUp() {
        service = new EmployerBasicInfoServiceImpl(
            employerContext,
            employerProfileRepository,
            employerBasicInfoRepository,
            siteMetadataResolver,
            employerProfileMapper
        );

        employerProfileId = UUID.randomUUID();
        employerProfile = EmployerProfileEntity.builder().id(employerProfileId).build();
        EmployerPrincipal principal = new EmployerPrincipal(null, employerProfile);
        when(employerContext.getCurrentEmployerOrThrow()).thenReturn(principal);
        lenient().when(employerBasicInfoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private EmployerBasicInfoPatchRequest patchWith(String field, String value) {
        JsonNullable<String> present = JsonNullable.of(value);
        JsonNullable<String> undefined = JsonNullable.undefined();
        return new EmployerBasicInfoPatchRequest(
            "companyName".equals(field) ? present : undefined,
            "taxNumber".equals(field) ? present : undefined,
            null,
            "companyEmail".equals(field) ? present : undefined,
            "imageUrl".equals(field) ? present : undefined,
            "address".equals(field) ? present : undefined,
            "websiteUrl".equals(field) ? present : undefined,
            "about".equals(field) ? present : undefined
        );
    }

    @Test
    void patchMyBasicInfo_noExistingBasicInfo_createsEmptyShellFirst() {
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.empty());

        service.patchMyBasicInfo(EMPTY_PATCH);

        ArgumentCaptor<EmployerBasicInfoEntity> captor = ArgumentCaptor.forClass(EmployerBasicInfoEntity.class);
        verify(employerBasicInfoRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertEquals(employerProfile, captor.getAllValues().get(0).getEmployerProfile());
    }

    @Test
    void patchMyBasicInfo_existingBasicInfo_reusesItWithoutCreatingAnother() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder()
            .id(UUID.randomUUID())
            .employerProfile(employerProfile)
            .build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        service.patchMyBasicInfo(EMPTY_PATCH);

        verify(employerBasicInfoRepository, org.mockito.Mockito.times(1)).save(existing);
    }

    @Test
    void patchMyBasicInfo_presentButBlankImageUrl_throws() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).employerProfile(employerProfile).build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        EmployerBasicInfoPatchRequest request = patchWith("imageUrl", "   ");

        ApiException exception = assertThrows(ApiException.class, () -> service.patchMyBasicInfo(request));

        assertEquals("profile.media.must_have_one_photo", exception.getMessage());
    }

    @Test
    void patchMyBasicInfo_presentNonBlankImageUrl_doesNotThrowAndSetsIt() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).employerProfile(employerProfile).build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        EmployerBasicInfoPatchRequest request = patchWith("imageUrl", "https://example.com/logo.png");

        service.patchMyBasicInfo(request);

        assertEquals("https://example.com/logo.png", existing.getImageUrl());
    }

    @Test
    void patchMyBasicInfo_undefinedFields_areLeftUnchanged() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder()
            .id(UUID.randomUUID())
            .employerProfile(employerProfile)
            .companyName("Original Co")
            .build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        service.patchMyBasicInfo(EMPTY_PATCH);

        assertEquals("Original Co", existing.getCompanyName());
    }

    @Test
    void patchMyBasicInfo_presentCompanyName_updatesIt() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder()
            .id(UUID.randomUUID())
            .employerProfile(employerProfile)
            .companyName("Old Name")
            .build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        EmployerBasicInfoPatchRequest request = patchWith("companyName", "New Name");

        service.patchMyBasicInfo(request);

        assertEquals("New Name", existing.getCompanyName());
    }

    @Test
    void patchMyBasicInfo_presentNullCompanyName_clearsIt() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder()
            .id(UUID.randomUUID())
            .employerProfile(employerProfile)
            .companyName("Old Name")
            .build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        EmployerBasicInfoPatchRequest request = new EmployerBasicInfoPatchRequest(
            JsonNullable.of(null),
            JsonNullable.undefined(),
            null,
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined()
        );

        service.patchMyBasicInfo(request);

        assertNull(existing.getCompanyName());
    }

    @Test
    void patchMyBasicInfo_companyTypeId_resolvesAndSetsCompanyType() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).employerProfile(employerProfile).build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        UUID companyTypeId = UUID.randomUUID();
        CompanyTypeOptionEntity companyType = new CompanyTypeOptionEntity();
        when(siteMetadataResolver.resolveCompanyTypeOrThrow(companyTypeId)).thenReturn(companyType);

        EmployerBasicInfoPatchRequest request = new EmployerBasicInfoPatchRequest(
            JsonNullable.undefined(), JsonNullable.undefined(), companyTypeId, JsonNullable.undefined(),
            JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );

        service.patchMyBasicInfo(request);

        assertEquals(companyType, existing.getCompanyType());
    }

    @Test
    void patchMyBasicInfo_nullCompanyTypeId_doesNotResolveOrChangeCompanyType() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).employerProfile(employerProfile).build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));

        service.patchMyBasicInfo(EMPTY_PATCH);

        verify(siteMetadataResolver, never()).resolveCompanyTypeOrThrow(any());
    }

    @Test
    void patchMyBasicInfo_returnsMappedResponse() {
        EmployerBasicInfoEntity existing = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).employerProfile(employerProfile).build();
        when(employerBasicInfoRepository.findByEmployerProfileId(employerProfileId)).thenReturn(Optional.of(existing));
        var expectedResponse = new com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse(
            existing.getId(), null, null, null, null, null, null, null, null, null
        );
        when(employerProfileMapper.toBasicInfoResponse(existing)).thenReturn(expectedResponse);

        var response = service.patchMyBasicInfo(EMPTY_PATCH);

        assertEquals(expectedResponse, response);
    }
}
