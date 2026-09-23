package com.padimasso.autocasting.application.castings.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingMediaCleanupServiceImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ---- isFolderEntry ----
    // Regression coverage for a real bug: Supabase Storage's list endpoint only returns the
    // immediate children of a prefix. Our layout nests role photos one level deeper
    // (.../castings/{castingId}/role-reference-photos/{file}), so a naive non-recursive list
    // against the casting prefix only ever saw the "role-reference-photos" folder pseudo-entry
    // itself — never the files inside it — and the resulting "delete key" wasn't a real object,
    // so casting/role deletion silently never removed anything from Supabase.

    @Test
    void isFolderEntry_nullIdAndNullMetadata_isFolder() {
        ObjectNode item = objectMapper.createObjectNode();
        item.putNull("id");
        item.putNull("metadata");
        item.put("name", "role-reference-photos");

        assertTrue(CastingMediaCleanupServiceImpl.isFolderEntry(item));
    }

    @Test
    void isFolderEntry_missingIdAndMetadata_isFolder() {
        // Jackson treats an absent field the same as a "null" JsonNode via .path(), so a
        // response that omits these fields entirely for folder pseudo-entries is still detected.
        ObjectNode item = objectMapper.createObjectNode();
        item.put("name", "role-reference-photos");

        assertTrue(CastingMediaCleanupServiceImpl.isFolderEntry(item));
    }

    @Test
    void isFolderEntry_hasIdAndMetadata_isNotFolder() {
        ObjectNode item = objectMapper.createObjectNode();
        item.put("id", "eaa8bdb5-2e00-4767-b5a9-d2502efe2196");
        item.putObject("metadata").put("size", 1234);
        item.put("name", "1700000000000.jpg");

        assertFalse(CastingMediaCleanupServiceImpl.isFolderEntry(item));
    }

    @Test
    void isFolderEntry_hasIdButNullMetadata_isNotFolder() {
        // A real file entry always has both id and metadata populated by Supabase; a partial
        // match (id present, metadata null) is treated as a file, not a folder, so we never
        // accidentally skip deleting a real object due to an unexpected shape.
        ObjectNode item = objectMapper.createObjectNode();
        item.put("id", "eaa8bdb5-2e00-4767-b5a9-d2502efe2196");
        item.putNull("metadata");
        item.put("name", "1700000000000.jpg");

        assertFalse(CastingMediaCleanupServiceImpl.isFolderEntry(item));
    }
}
