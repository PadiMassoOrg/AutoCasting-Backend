package com.padimasso.autocasting.application.shared.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageHydrationTest {

    private record Item(int id, String label) {
    }

    @Test
    void hydrate_keepsPageOrderWhenLoaderReturnsDifferentOrder() {
        var page = new PageImpl<>(List.of(new Item(3, "a"), new Item(1, "a"), new Item(2, "a")), PageRequest.of(0, 3), 10);

        Page<Item> result = PageHydration.hydrate(
            page,
            Item::id,
            ids -> List.of(new Item(1, "full"), new Item(2, "full"), new Item(3, "full"))
        );

        assertEquals(List.of(3, 1, 2), result.getContent().stream().map(Item::id).toList());
        assertTrue(result.getContent().stream().allMatch(i -> i.label().equals("full")));
    }

    @Test
    void hydrate_preservesPageMetadata() {
        var page = new PageImpl<>(List.of(new Item(1, "a"), new Item(2, "a")), PageRequest.of(1, 2), 7);

        Page<Item> result = PageHydration.hydrate(page, Item::id, ids -> List.of(new Item(1, "full"), new Item(2, "full")));

        assertEquals(7, result.getTotalElements());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
        assertTrue(result.hasNext());
    }

    @Test
    void hydrate_dropsIdsMissingFromLoader() {
        var page = new PageImpl<>(List.of(new Item(1, "a"), new Item(2, "a")), PageRequest.of(0, 2), 2);

        Page<Item> result = PageHydration.hydrate(page, Item::id, ids -> List.of(new Item(2, "full")));

        assertEquals(List.of(2), result.getContent().stream().map(Item::id).toList());
    }

    @Test
    void hydrate_emptyPageSkipsLoader() {
        Page<Item> page = new PageImpl<>(List.of(), PageRequest.of(0, 5), 0);

        Page<Item> result = PageHydration.hydrate(page, Item::id, ids -> {
            throw new AssertionError("loader must not be called");
        });

        assertSame(page, result);
    }
}
