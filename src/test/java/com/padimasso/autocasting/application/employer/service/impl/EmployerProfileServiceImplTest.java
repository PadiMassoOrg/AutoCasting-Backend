package com.padimasso.autocasting.application.employer.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerProfileServiceImplTest {

    @Mock
    private EmployerContext employerContext;
    @Mock
    private AuthContext authContext;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private EmployerProfileMapper employerProfileMapper;

    private EmployerProfileServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmployerProfileServiceImpl(employerContext, authContext, employerProfileRepository, employerProfileMapper);
    }

    @Test
    void getMyProfile_delegatesToMapperWithCurrentEmployerAndUser() {
        UserEntity user = new UserEntity();
        EmployerProfileEntity profile = EmployerProfileEntity.builder().id(UUID.randomUUID()).build();
        EmployerPrincipal principal = new EmployerPrincipal(user, profile);
        when(employerContext.getCurrentEmployerOrThrow()).thenReturn(principal);

        EmployerProfileResponse expected = new EmployerProfileResponse(profile.getId(), null, null, null, null, null);
        when(employerProfileMapper.toProfileResponse(profile, user)).thenReturn(expected);

        EmployerProfileResponse response = service.getMyProfile();

        assertEquals(expected, response);
    }
}
