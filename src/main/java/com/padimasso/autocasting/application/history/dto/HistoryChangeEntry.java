package com.padimasso.autocasting.application.history.dto;

public record HistoryChangeEntry(
    String fieldKey,
    Object previousValue,
    Object newValue
) {
}
