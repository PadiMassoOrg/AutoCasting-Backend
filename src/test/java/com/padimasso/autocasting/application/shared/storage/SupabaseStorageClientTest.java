package com.padimasso.autocasting.application.shared.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupabaseStorageClientTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JsonNode json(String raw) throws Exception {
        return MAPPER.readTree(raw);
    }

    // Regression coverage for a real bug: Supabase Storage's list endpoint only returns the immediate children
    // of a prefix, so nested folders come back as pseudo-entries that must be recursed into. Without that,
    // casting and role deletion silently never removed anything from Supabase.

    @Test
    void isFolderEntry_nullIdAndNullMetadata_isFolder() throws Exception {
        assertTrue(SupabaseStorageClient.isFolderEntry(json("{\"name\":\"role-reference-photos\",\"id\":null,\"metadata\":null}")));
    }

    @Test
    void isFolderEntry_missingIdAndMetadata_isFolder() throws Exception {
        assertTrue(SupabaseStorageClient.isFolderEntry(json("{\"name\":\"role-reference-photos\"}")));
    }

    @Test
    void isFolderEntry_hasIdAndMetadata_isNotFolder() throws Exception {
        assertFalse(SupabaseStorageClient.isFolderEntry(
            json("{\"name\":\"1700000000000.jpg\",\"id\":\"eaa8bdb5\",\"metadata\":{\"size\":1234}}")));
    }

    @Test
    void isFolderEntry_hasIdButNullMetadata_isNotFolder() throws Exception {
        // A partial match is treated as a file, so an unexpected shape never makes us skip a real object.
        assertFalse(SupabaseStorageClient.isFolderEntry(
            json("{\"name\":\"1700000000000.jpg\",\"id\":\"eaa8bdb5\",\"metadata\":null}")));
    }

    @Test
    void parseCreatedAt_readsTheStorageTimestamp() throws Exception {
        assertEquals(Instant.parse("2026-10-01T12:30:00.000Z"),
            SupabaseStorageClient.parseCreatedAt(json("{\"created_at\":\"2026-10-01T12:30:00.000Z\"}")));
    }

    @Test
    void parseCreatedAt_isNullWhenMissingOrMalformed() throws Exception {
        assertNull(SupabaseStorageClient.parseCreatedAt(json("{}")));
        assertNull(SupabaseStorageClient.parseCreatedAt(json("{\"created_at\":\"yesterday\"}")));
    }
}
