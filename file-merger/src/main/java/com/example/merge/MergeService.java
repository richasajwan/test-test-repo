package com.example.merge;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Merges several CSV files into one CSV.
 *
 * <ul>
 *   <li>The output columns are the union of all input headers, in first-seen order.</li>
 *   <li>A leading {@code source_file} column records which input each row came from.</li>
 *   <li>Missing values are written as empty cells.</li>
 * </ul>
 *
 * Values are split on commas; quoted fields containing commas are not supported.
 */
@ApplicationScoped
public class MergeService {

    static final String SOURCE_COLUMN = "source_file";

    public MergeResult merge(List<Path> inputs, Path output) throws IOException {
        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("No input files configured");
        }

        Set<String> columns = new LinkedHashSet<>();
        List<Map<String, String>> rows = new ArrayList<>();
        Map<String, Integer> rowCounts = new LinkedHashMap<>();

        for (Path input : inputs) {
            List<String> lines = readLines(input);
            if (lines.isEmpty()) {
                throw new IllegalArgumentException("Input file is empty: " + input);
            }

            List<String> header = split(lines.get(0));
            columns.addAll(header);

            String sourceName = input.getFileName().toString();
            int count = 0;
            for (String line : lines.subList(1, lines.size())) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> values = split(line);
                if (values.size() != header.size()) {
                    throw new IllegalArgumentException("Column count mismatch in " + input
                            + ": expected " + header.size() + " but got " + values.size()
                            + " in line: " + line);
                }
                Map<String, String> row = new LinkedHashMap<>();
                row.put(SOURCE_COLUMN, sourceName);
                for (int i = 0; i < header.size(); i++) {
                    row.put(header.get(i), values.get(i));
                }
                rows.add(row);
                count++;
            }
            rowCounts.put(sourceName, count);
        }

        List<String> outputColumns = new ArrayList<>();
        outputColumns.add(SOURCE_COLUMN);
        outputColumns.addAll(columns);

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", outputColumns)).append('\n');
        for (Map<String, String> row : rows) {
            List<String> cells = new ArrayList<>(outputColumns.size());
            for (String column : outputColumns) {
                cells.add(row.getOrDefault(column, ""));
            }
            csv.append(String.join(",", cells)).append('\n');
        }

        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.writeString(output, csv.toString(), StandardCharsets.UTF_8);

        return new MergeResult(output, rows.size(), rowCounts);
    }

    private static List<String> readLines(Path input) throws IOException {
        if (!Files.isRegularFile(input)) {
            throw new IllegalArgumentException("Input file not found: " + input.toAbsolutePath());
        }
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(input, StandardCharsets.UTF_8)) {
            // Strip a UTF-8 BOM from the first line and any trailing carriage returns
            String cleaned = line.replace("﻿", "").replace("\r", "");
            lines.add(cleaned);
        }
        return lines;
    }

    private static List<String> split(String line) {
        List<String> parts = new ArrayList<>();
        for (String part : line.split(",", -1)) {
            parts.add(part.trim());
        }
        return parts;
    }
}
