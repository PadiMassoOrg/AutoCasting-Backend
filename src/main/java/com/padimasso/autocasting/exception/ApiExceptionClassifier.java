package com.padimasso.autocasting.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Component
public class ApiExceptionClassifier {

    private static final Set<String> CONFLICT_EXACT_KEYS = Set.of(
        AUTH_USER_EXISTS,
        AUTH_PASSWORD_RESET_EXTERNAL,
        APPLICATIONS_ALREADY_APPLIED,
        CASTINGS_INVALID_STATUS_TRANSITION,
        CASTINGS_NOT_PUBLISHABLE,
        CASTINGS_DEADLINE_PASSED,
        CASTING_ROLE_REQUIREMENT_ALREADY_EXISTS
    );

    private static final Set<String> INTERNAL_EXACT_KEYS = Set.of(
        AUTH_INVALID_PLAN,
        AUTH_INVALID_ROLE,
        "mail.send_failed",
        "legal.hash_generation_failed"
    );

    public ApiException classify(Exception exception) {
        if (exception instanceof ApiException apiException) {
            return apiException;
        }

        if (exception instanceof OAuth2AuthenticationException oauthException) {
            String errorCode = oauthException.getError() != null
                ? oauthException.getError().getErrorCode()
                : oauthException.getMessage();
            return ApiException.unauthorized(errorCode == null || errorCode.isBlank() ? "oauth.google.failure" : errorCode);
        }

        if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            return ApiException.unauthorized(AUTH_INVALID_CREDENTIALS);
        }

        if (exception instanceof InsufficientAuthenticationException insufficientAuthenticationException) {
            return ApiException.unauthorized(resolveAuthenticationMessage(insufficientAuthenticationException));
        }

        if (exception instanceof AuthenticationException) {
            return ApiException.unauthorized(AUTH_NOT_AUTHENTICATED);
        }

        if (exception instanceof AccessDeniedException) {
            return ApiException.forbidden(AUTH_ACCESS_DENIED);
        }

        if (exception instanceof MissingServletRequestParameterException missingParameterException) {
            return ApiException.badRequest(GENERAL_MISSING_PARAMETER, missingParameterException.getParameterName());
        }

        if (exception instanceof MethodArgumentTypeMismatchException mismatchException) {
            return ApiException.badRequest(GENERAL_INVALID_PARAMETER, mismatchException.getName());
        }

        if (exception instanceof HttpMessageNotReadableException) {
            return ApiException.badRequest(GENERAL_INVALID_REQUEST_BODY);
        }

        if (exception instanceof HttpRequestMethodNotSupportedException methodNotSupportedException) {
            return new ApiException(HttpStatus.METHOD_NOT_ALLOWED, GENERAL_METHOD_NOT_ALLOWED, methodNotSupportedException.getMethod());
        }

        if (exception instanceof NoResourceFoundException) {
            return ApiException.notFound(GENERAL_ENDPOINT_NOT_FOUND);
        }

        if (exception instanceof NoSuchElementException) {
            return ApiException.notFound(GENERAL_RESOURCE_NOT_FOUND);
        }

        if (exception instanceof DataIntegrityViolationException) {
            return ApiException.conflict(GENERAL_DATA_INTEGRITY_VIOLATION);
        }

        if (exception instanceof ResponseStatusException responseStatusException) {
            HttpStatusCode statusCode = responseStatusException.getStatusCode();
            HttpStatus status = HttpStatus.resolve(statusCode.value());
            String reason = responseStatusException.getReason();
            String messageKey = (reason == null || reason.isBlank()) ? GENERAL_UNEXPECTED : reason;
            Object[] args = reason == null || reason.isBlank()
                ? new Object[]{safeMessage(exception)}
                : new Object[0];
            return new ApiException(status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status, messageKey, args);
        }

        if (exception instanceof IllegalArgumentException illegalArgumentException) {
            return classifyKeyedException(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        if (exception instanceof IllegalStateException illegalStateException) {
            return classifyKeyedException(illegalStateException.getMessage(), HttpStatus.CONFLICT, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String message = safeMessage(exception);
        return ApiException.internal(GENERAL_UNEXPECTED, message);
    }

    private ApiException classifyKeyedException(String rawMessage, HttpStatus keyedFallback, HttpStatus plainTextFallback) {
        ParsedMessage parsed = parseMessage(rawMessage);

        if (!looksLikeMessageKey(parsed.key())) {
            return new ApiException(plainTextFallback, parsed.key(), parsed.args());
        }

        return new ApiException(inferStatus(parsed.key(), keyedFallback), parsed.key(), parsed.args());
    }

    private HttpStatus inferStatus(String messageKey, HttpStatus fallbackStatus) {
        if (messageKey == null || messageKey.isBlank()) {
            return fallbackStatus;
        }

        if (messageKey.equals(AUTH_NOT_AUTHENTICATED)
            || messageKey.equals(AUTH_INVALID_PRINCIPAL)
            || messageKey.equals(AUTH_INVALID_CREDENTIALS)
            || messageKey.equals(AUTH_INVALID_TOKEN)
            || messageKey.equals(AUTH_TOKEN_EXPIRED)
            || messageKey.equals("oauth.google.failure")
            || messageKey.equals("oauth.google.user_missing_email")
            || messageKey.equals("unauthorized")) {
            return HttpStatus.UNAUTHORIZED;
        }

        if (messageKey.endsWith(".forbidden")
            || messageKey.contains(".forbidden.")
            || messageKey.equals(AUTH_ACCESS_DENIED)) {
            return HttpStatus.FORBIDDEN;
        }

        if (INTERNAL_EXACT_KEYS.contains(messageKey)) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (messageKey.endsWith(".not_found")
            || messageKey.contains(".not_found.")
            || messageKey.equals(GENERAL_RESOURCE_NOT_FOUND)
            || messageKey.equals(GENERAL_ENDPOINT_NOT_FOUND)
            || messageKey.endsWith(".not_found_or_forbidden")) {
            return HttpStatus.NOT_FOUND;
        }

        if (CONFLICT_EXACT_KEYS.contains(messageKey)) {
            return HttpStatus.CONFLICT;
        }

        if (messageKey.contains(".already_")
            || messageKey.endsWith(".mismatch")
            || messageKey.contains(".required")
            || messageKey.contains(".missing")
            || messageKey.contains(".invalid")
            || messageKey.endsWith(".email_invalid")
            || messageKey.endsWith(".password_length")) {
            return HttpStatus.BAD_REQUEST;
        }

        return fallbackStatus;
    }

    private String resolveAuthenticationMessage(InsufficientAuthenticationException exception) {
        String message = exception.getMessage();
        if ("token expired".equalsIgnoreCase(message)) {
            return AUTH_TOKEN_EXPIRED;
        }
        if ("invalid token".equalsIgnoreCase(message)) {
            return AUTH_INVALID_TOKEN;
        }
        return AUTH_NOT_AUTHENTICATED;
    }

    private ParsedMessage parseMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return new ParsedMessage(GENERAL_UNEXPECTED, new Object[]{"No message available"});
        }

        String[] parts = rawMessage.split("\\|");
        String key = parts[0];
        Object[] args = Arrays.copyOfRange(parts, 1, parts.length, Object[].class);
        return new ParsedMessage(key, args);
    }

    private boolean looksLikeMessageKey(String message) {
        return message != null
            && !message.isBlank()
            && !message.contains(" ")
            && message.matches("[A-Za-z0-9_.-]+");
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
            ? exception.getClass().getSimpleName()
            : exception.getMessage();
    }

    private record ParsedMessage(String key, Object[] args) {
    }
}
