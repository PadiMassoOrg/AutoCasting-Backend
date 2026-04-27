package com.padimasso.autocasting.application.legal.security;

import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.exception.ApiErrorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LegalAcceptanceEnforcementFilterTest {

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturn428WhenAuthenticatedUserHasNotAcceptedCurrentLegalDocuments() throws Exception {
        LegalService legalService = mock(LegalService.class);
        ApiErrorFactory apiErrorFactory = mock(ApiErrorFactory.class);
        LegalAcceptanceEnforcementFilter filter = new LegalAcceptanceEnforcementFilter(legalService, apiErrorFactory);

        UserEntity user = UserEntity.builder()
            .id(UUID.randomUUID())
            .email("seed-user@autocasting.app")
            .roles(Set.of(RoleEntity.builder().code("TALENT").nameStringCode("role.talent").build()))
            .build();

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        when(legalService.hasAcceptedCurrentRequired(user.getId(), "es")).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/talent");
        request.addHeader("Accept-Language", "es");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jakarta.servlet.FilterChain chain = mock(jakarta.servlet.FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(legalService).hasAcceptedCurrentRequired(user.getId(), "es");
        verify(apiErrorFactory).write(
            eq(request),
            eq(response),
            eq(HttpStatus.PRECONDITION_REQUIRED),
            eq("legal.acceptance.required"),
            any(Object[].class)
        );
        verify(chain, never()).doFilter(any(), any());
    }
}

