package com.padimasso.autocasting.application.shared.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PageHydration {

    private PageHydration() {
    }

    /**
     * Swaps the content of a page fetched without collection joins for the same entities loaded with
     * their collections, keeping the page order and metadata. Avoids paginating in memory (HHH90003004).
     */
    public static <T, I> Page<T> hydrate(
        Page<T> page,
        Function<T, I> idOf,
        Function<List<I>, List<T>> loadByIds
    ) {
        if (page.isEmpty()) {
            return page;
        }

        List<I> ids = page.getContent().stream().map(idOf).toList();
        Map<I, T> byId = loadByIds.apply(ids).stream()
            .collect(Collectors.toMap(idOf, Function.identity(), (a, b) -> a));

        List<T> ordered = ids.stream()
            .map(byId::get)
            .filter(Objects::nonNull)
            .toList();

        return new PageImpl<>(ordered, page.getPageable(), page.getTotalElements());
    }
}
