package com.padimasso.autocasting.application.shared.web;

import java.util.List;

public record SliceResponse<T>(
    List<T> items,
    boolean hasNext,
    int page,
    int size
) {}

