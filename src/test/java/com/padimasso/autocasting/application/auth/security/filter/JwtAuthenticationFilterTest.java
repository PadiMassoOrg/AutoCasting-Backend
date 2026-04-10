package com.padimasso.autocasting.application.auth.security.filter;

import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.JwtService;
import com.padimasso.autocasting.exception.JwtAuthEntryPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_TOKEN_EXPIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtAuthEntryPoint authenticationEntryPoint;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void handlesExpiredTokensReportedAsIllegalArgumentException() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userRepository, authenticationEntryPoint);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        request.addHeader("Authorization", "Bearer expired-token");
        doThrow(new IllegalArgumentException(AUTH_TOKEN_EXPIRED)).when(jwtService).extractEmail("expired-token");

        filter.doFilter(request, response, filterChain);

        assertEquals(AUTH_TOKEN_EXPIRED, request.getAttribute("auth_error_code"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint).commence(
            eq(request),
            eq(response),
            argThat(exception ->
                exception instanceof InsufficientAuthenticationException
                    && "token expired".equals(exception.getMessage()))
        );
        verify(filterChain, never()).doFilter(request, response);
        verifyNoInteractions(userRepository);
    }
}
