package com.padimasso.autocasting.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    private final ApiErrorFactory apiErrorFactory;
    private final ApiExceptionClassifier apiExceptionClassifier;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception,
                                                                       HttpServletRequest request,
                                                                       Locale locale) {
        Map<String, ApiErrorMessage> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String messageKey = error.getDefaultMessage();
            fieldErrors.putIfAbsent(
                error.getField(),
                apiErrorFactory.buildMessage(messageKey, null)
            );
        });

        ApiErrorResponse body = apiErrorFactory.build(
            org.springframework.http.HttpStatus.BAD_REQUEST,
            "general.validation_failed",
            null,
            request.getRequestURI(),
            locale,
            fieldErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAnyException(Exception exception,
                                                               HttpServletRequest request,
                                                               Locale locale) {
        ApiException apiException = apiExceptionClassifier.classify(exception);

        if (apiException.getStatus().is5xxServerError()) {
            log.error("Unhandled exception at {} -> {}", request.getRequestURI(), exception.getMessage(), exception);
        } else {
            log.debug("Handled exception at {} -> {}", request.getRequestURI(), exception.getMessage());
        }

        ApiErrorResponse body = apiErrorFactory.build(apiException, request.getRequestURI(), locale);
        return ResponseEntity.status(apiException.getStatus()).body(body);
    }
}
