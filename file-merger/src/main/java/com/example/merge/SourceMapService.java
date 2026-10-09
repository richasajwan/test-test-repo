package com.example.merge;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a source map from the merged CSV: the key is the entity ("customer", "order"),
 * the value is every merged row that came from that entity's source file.
 */
@ApplicationScoped
public class SourceMapService {

    /** Source file name to map key. Files not listed here (e.g. products.csv) are left out. */
    static final Map<String, String> KEY_BY_SOURCE = Map.of(
            "customers.csv", "customer",
            "orders.csv", "order");

    @Inject
    MergeConfig config;

    public Map<String, List<SourceRow>> sourceMap() throws IOException {
if (!Files.exists(merged)) {
        if (!Files.isRegularFile(merged)) {
            throw new MergedFileMissingException(merged);
        }

        Map<String, List<SourceRow>> result = new LinkedHashMap<>();
        KEY_BY_SOURCE.values().forEach(key -> result.put(key, new ArrayList<>()));

        List<String> lines = Files.readAllLines(merged, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return result;
        }
        List<String> header = split(lines.get(0));

        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) {
                continue;
            }
            List<String> values = split(line);
            Map<String, String> cells = new LinkedHashMap<>();
            for (int i = 0; i < header.size(); i++) {
                cells.put(header.get(i), values.get(i));
            }

String entityKey = KEY_BY_SOURCE.get(cells.get("source_file"));
            if (key != null) {
                result.get(key).add(toRow(cells));
            }
        }
        return result;
    }

    private static SourceRow toRow(Map<String, String> cells) {
        return new SourceRow(
                cells.get("source_file"),
                emptyToNull(cells.get("customer_id")),
                emptyToNull(cells.get("name")),
                emptyToNull(cells.get("email")),
                emptyToNull(cells.get("order_id")),
                emptyToNull(cells.get("product_id")),
                emptyToNull(cells.get("quantity")),
                emptyToNull(cells.get("product_name")),
                emptyToNull(cells.get("price")));
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static List<String> split(String line) {
        List<String> parts = new ArrayList<>();
        for (String part : line.replace("\r", "").split(",", -1)) {
            parts.add(part.trim());
        }
        return parts;
    }
}
