package com.padimasso.autocasting.application.admin.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Turns before/after snapshots of a talent or employer profile section into a compact audit trail: only what changed.
 *
 * <ul>
 *   <li>Scalars and single metadata values: {@code section.field: previous -> new}.</li>
 *   <li>Lists of items with an identity (skills, professions, credits, education, social links): one
 *       {@code .added} and one {@code .removed} entry naming only the items involved, plus one entry per
 *       changed field of items that stayed ({@code credits[Hamlet].year}).</li>
 *   <li>Lists of plain values (other pictures) are compared by position.</li>
 *   <li>Bookkeeping ({@code id}, {@code modifiedAt}), collection order and unchanged data never appear, and
 *       Storage URLs are shortened to the file name.</li>
 * </ul>
 */
public final class ProfileChangeDiff {

    private static final Set<String> IGNORED_FIELDS = Set.of("id", "optionId", "modifiedAt");
    private static final String STORAGE_PATH = "/storage/v1/object/public/";

    private ProfileChangeDiff() {
    }

    public static List<HistoryChangeEntry> diff(ObjectMapper mapper, String section, JsonNode before, JsonNode after) {
        var entries = new ArrayList<HistoryChangeEntry>();

        if (isObject(before) || isObject(after)) {
            for (String field : fieldNames(before, after)) {
                diffValue(mapper, entries, section + "." + field, child(before, field), child(after, field));
            }
        } else {
            diffValue(mapper, entries, section, before, after);
        }

        return entries;
    }

    private static void diffValue(ObjectMapper mapper, List<HistoryChangeEntry> entries, String key, JsonNode previous, JsonNode next) {
        if (isEmpty(previous) && isEmpty(next)) return;

        if (isArray(previous) || isArray(next)) {
            diffList(mapper, entries, key, items(previous), items(next));
            return;
        }

        if ((isObject(previous) || isObject(next)) && !isMetadata(previous) && !isMetadata(next)) {
            for (String field : fieldNames(previous, next)) {
                diffValue(mapper, entries, key + "." + field, child(previous, field), child(next, field));
            }
            return;
        }

        if (isObject(previous) || isObject(next)) {
            if (sameIdentity(previous, next)) return;
            entries.add(new HistoryChangeEntry(key, toValue(mapper, trimObject(previous)), toValue(mapper, trimObject(next))));
            return;
        }

        if (previous != null && previous.equals(next)) return;
        entries.add(new HistoryChangeEntry(key, scalar(mapper, previous), scalar(mapper, next)));
    }

    private static void diffList(ObjectMapper mapper, List<HistoryChangeEntry> entries, String key, List<JsonNode> previous, List<JsonNode> next) {
        boolean plainValues = previous.stream().allMatch(item -> !item.isObject()) && next.stream().allMatch(item -> !item.isObject());
        if (plainValues) {
            for (int i = 0; i < Math.max(previous.size(), next.size()); i++) {
                JsonNode before = i < previous.size() ? previous.get(i) : null;
                JsonNode after = i < next.size() ? next.get(i) : null;
                diffValue(mapper, entries, key + "[" + (i + 1) + "]", before, after);
            }
            return;
        }

        Map<String, JsonNode> previousById = byIdentity(previous);
        Map<String, JsonNode> nextById = byIdentity(next);

        List<JsonNode> added = new ArrayList<>();
        List<JsonNode> removed = new ArrayList<>();
        nextById.forEach((id, item) -> {
            if (!previousById.containsKey(id)) added.add(item);
        });
        previousById.forEach((id, item) -> {
            if (!nextById.containsKey(id)) removed.add(item);
        });

        if (!added.isEmpty()) entries.add(new HistoryChangeEntry(key + ".added", null, labels(mapper, added)));
        if (!removed.isEmpty()) entries.add(new HistoryChangeEntry(key + ".removed", labels(mapper, removed), null));

        nextById.forEach((id, item) -> {
            JsonNode old = previousById.get(id);
            if (old == null) return;
            String label = shortLabel(item);
            for (String field : fieldNames(old, item)) {
                diffValue(mapper, entries, key + "[" + label + "]." + field, child(old, field), child(item, field));
            }
        });
    }

    private static Map<String, JsonNode> byIdentity(List<JsonNode> items) {
        Map<String, JsonNode> result = new LinkedHashMap<>();
        for (JsonNode item : items) {
            result.put(identity(item), item);
        }
        return result;
    }

    private static String identity(JsonNode item) {
        for (String field : List.of("id", "optionId")) {
            JsonNode value = item.get(field);
            if (value != null && !value.isNull()) return value.asText();
        }
        return item.toString();
    }

    private static boolean sameIdentity(JsonNode previous, JsonNode next) {
        if (!isObject(previous) || !isObject(next)) return false;
        JsonNode previousId = previous.get("id");
        return previousId != null && previousId.equals(next.get("id"));
    }

    /** Readable names for added/removed items: metadata items keep a translatable code, others become text. */
    private static Object labels(ObjectMapper mapper, List<JsonNode> items) {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        for (JsonNode item : items) {
            String name = textOf(item, "projectName");
            if (name == null) name = textOf(item, "courseName");
            if (name != null) {
                String detail = textOf(item, "year");
                if (detail == null) detail = textOf(item, "graduationYear");
                result.add(detail == null ? name : name + " (" + detail + ")");
            } else if (textOf(item, "stringCode") != null) {
                ObjectNode code = JsonNodeFactory.instance.objectNode();
                code.put("stringCode", textOf(item, "stringCode"));
                result.add(code);
            } else {
                result.add(item.toString());
            }
        }
        return mapper.convertValue(result, Object.class);
    }

    private static String shortLabel(JsonNode item) {
        for (String field : List.of("projectName", "courseName")) {
            String value = textOf(item, field);
            if (value != null) return value;
        }
        String code = textOf(item, "stringCode");
        if (code != null) return code.substring(code.lastIndexOf('.') + 1);
        return identity(item);
    }

    private static String textOf(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() || value.asText().isBlank() ? null : value.asText();
    }

    private static Object scalar(ObjectMapper mapper, JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isTextual()) {
            String text = node.asText();
            if (text.isBlank()) return null;
            int path = text.indexOf(STORAGE_PATH);
            return path >= 0 ? text.substring(text.lastIndexOf('/') + 1) : text;
        }
        return mapper.convertValue(node, Object.class);
    }

    private static Object toValue(ObjectMapper mapper, JsonNode node) {
        return node == null || node.isNull() ? null : mapper.convertValue(node, Object.class);
    }

    /** Single metadata values (gender, ethnicity...) only need their code to be readable. */
    private static JsonNode trimObject(JsonNode node) {
        if (!isObject(node)) return node;
        String code = textOf(node, "stringCode");
        if (code == null) return node;
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("stringCode", code);
        return result;
    }

    private static Set<String> fieldNames(JsonNode first, JsonNode second) {
        Set<String> names = new LinkedHashSet<>();
        collect(first, names);
        collect(second, names);
        names.removeAll(IGNORED_FIELDS);
        return names;
    }

    private static void collect(JsonNode node, Set<String> names) {
        if (!isObject(node)) return;
        Iterator<String> iterator = node.fieldNames();
        iterator.forEachRemaining(names::add);
    }

    private static JsonNode child(JsonNode node, String field) {
        return isObject(node) ? node.get(field) : null;
    }

    private static List<JsonNode> items(JsonNode node) {
        List<JsonNode> result = new ArrayList<>();
        if (isArray(node)) node.forEach(result::add);
        return result;
    }

    private static boolean isMetadata(JsonNode node) {
        return isObject(node) && node.has("stringCode");
    }

    private static boolean isObject(JsonNode node) {
        return node != null && node.isObject();
    }

    private static boolean isArray(JsonNode node) {
        return node != null && node.isArray();
    }

    private static boolean isEmpty(JsonNode node) {
        return node == null
            || node.isNull()
            || (node.isTextual() && node.asText().isBlank())
            || (node.isContainerNode() && node.isEmpty());
    }
}
