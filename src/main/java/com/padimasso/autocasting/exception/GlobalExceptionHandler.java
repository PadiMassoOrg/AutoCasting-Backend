package com.padimasso.autocasting.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String messageKey,
            Object[] args,
            HttpServletRequest request,
            Locale locale
    ) {
        String message = messageSource.getMessage(messageKey, args, locale);
        var error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(error);
    }


    // Authentication
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request,
            Locale locale
    ) {
        // Ejemplo de mensaje: "user.exists|test@email.com"
        String[] parts = ex.getMessage().split("\\|");
        String key = parts[0];
        Object[] args = Arrays.copyOfRange(parts, 1, parts.length);

        return buildResponse(HttpStatus.CONFLICT, key, args, request, locale);
    }

    // General
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAnyException(
            Exception ex,
            HttpServletRequest request,
            Locale locale
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "general.unexpected",
                new Object[]{ex.getMessage()},
                request,
                locale
        );
    }
}