package com.padimasso.autocasting.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_NOT_AUTHENTICATED;

@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ApiErrorFactory apiErrorFactory;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String messageKey = (String) request.getAttribute("auth_error_code");
        if (messageKey == null || messageKey.isBlank()) {
            messageKey = AUTH_NOT_AUTHENTICATED;
        }

        apiErrorFactory.write(request, response, HttpStatus.UNAUTHORIZED, messageKey, null);
    }
}
