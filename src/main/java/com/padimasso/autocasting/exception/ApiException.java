package com.padimasso.autocasting.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String messageKey;
    private final transient Object[] args;

    public ApiException(HttpStatus status, String messageKey, Object... args) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
        this.args = args == null ? new Object[0] : args.clone();
    }

    public ApiException(HttpStatus status, String messageKey, Throwable cause, Object... args) {
        super(messageKey, cause);
        this.status = status;
        this.messageKey = messageKey;
        this.args = args == null ? new Object[0] : args.clone();
    }

    public static ApiException badRequest(String messageKey, Object... args) {
        return new ApiException(HttpStatus.BAD_REQUEST, messageKey, args);
    }

    public static ApiException unauthorized(String messageKey, Object... args) {
        return new ApiException(HttpStatus.UNAUTHORIZED, messageKey, args);
    }

    public static ApiException forbidden(String messageKey, Object... args) {
        return new ApiException(HttpStatus.FORBIDDEN, messageKey, args);
    }

    public static ApiException notFound(String messageKey, Object... args) {
        return new ApiException(HttpStatus.NOT_FOUND, messageKey, args);
    }

    public static ApiException conflict(String messageKey, Object... args) {
        return new ApiException(HttpStatus.CONFLICT, messageKey, args);
    }

    public static ApiException internal(String messageKey, Object... args) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, messageKey, args);
    }

    public static ApiException internal(Throwable cause, String messageKey, Object... args) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, messageKey, cause, args);
    }
}
