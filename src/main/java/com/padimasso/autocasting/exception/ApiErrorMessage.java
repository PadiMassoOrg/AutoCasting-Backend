package com.padimasso.autocasting.exception;

import java.util.List;

public record ApiErrorMessage(
    String message,
    List<String> messageArgs
) {
}
