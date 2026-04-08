package com.padimasso.autocasting.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ApiErrorFactory apiErrorFactory;
    private final ApiExceptionClassifier apiExceptionClassifier;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        ApiException apiException = apiExceptionClassifier.classify(exception);
        apiErrorFactory.write(
            request,
            response,
            apiException.getStatus(),
            apiException.getMessageKey(),
            apiException.getArgs()
        );
    }
}
