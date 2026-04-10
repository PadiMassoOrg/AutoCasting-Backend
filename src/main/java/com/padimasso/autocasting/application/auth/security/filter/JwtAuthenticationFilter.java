package com.padimasso.autocasting.application.auth.security.filter;

import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.JwtService;
import com.padimasso.autocasting.exception.JwtAuthEntryPoint;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_INVALID_TOKEN;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_TOKEN_EXPIRED;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtAuthEntryPoint authenticationEntryPoint;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String email = jwtService.extractEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = userRepository.findByEmail(email).orElse(null);
                if (user != null && jwtService.isTokenValid(jwt, user)) {
                    var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            handleAuthenticationFailure(request, response, AUTH_TOKEN_EXPIRED, "token expired", ex);
        } catch (JwtException ex) {
            handleAuthenticationFailure(request, response, AUTH_INVALID_TOKEN, "invalid token", ex);
        } catch (IllegalArgumentException ex) {
            String errorCode = AUTH_TOKEN_EXPIRED.equals(ex.getMessage()) ? AUTH_TOKEN_EXPIRED : AUTH_INVALID_TOKEN;
            String authMessage = AUTH_TOKEN_EXPIRED.equals(errorCode) ? "token expired" : "invalid token";
            handleAuthenticationFailure(request, response, errorCode, authMessage, ex);
        }
    }

    private void handleAuthenticationFailure(HttpServletRequest request,
                                             HttpServletResponse response,
                                             String errorCode,
                                             String authMessage,
                                             Exception exception) throws IOException {
        SecurityContextHolder.clearContext();
        request.setAttribute("auth_error_code", errorCode);
        authenticationEntryPoint.commence(request, response,
            new InsufficientAuthenticationException(authMessage, exception));
    }
}
