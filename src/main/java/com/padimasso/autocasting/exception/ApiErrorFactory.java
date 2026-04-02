package com.padimasso.autocasting.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiErrorFactory {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public ApiErrorResponse build(HttpStatus status,
                                  String messageKey,
                                  Object[] args,
                                  String path,
                                  Locale locale) {
        return build(status, messageKey, args, path, locale, null);
    }

    public ApiErrorResponse build(HttpStatus status,
                                  String messageKey,
                                  Object[] args,
                                  String path,
                                  Locale locale,
                                  Map<String, String> errors) {
        return ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .message(resolveErrorCode(messageKey))
            .errors(errors)
            .path(path)
            .build();
    }

    public ApiErrorResponse build(ApiException exception,
                                  String path,
                                  Locale locale) {
        return build(exception.getStatus(), exception.getMessageKey(), exception.getArgs(), path, locale);
    }

    public void write(HttpServletRequest request,
                      HttpServletResponse response,
                      HttpStatus status,
                      String messageKey,
                      Object[] args) throws IOException {
        write(request, response, status, messageKey, args, null);
    }

    public void write(HttpServletRequest request,
                      HttpServletResponse response,
                      HttpStatus status,
                      String messageKey,
                      Object[] args,
                      Map<String, String> errors) throws IOException {
        Locale locale = RequestContextUtils.getLocale(request);
        ApiErrorResponse body = build(status, messageKey, args, request.getRequestURI(), locale, errors);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    public String resolveErrorCode(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) {
            return "general.unexpected";
        }

        return messageSource.getMessage(messageKey, null, messageKey, Locale.ROOT);
    }
}
