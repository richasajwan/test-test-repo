package com.example.merge;

import java.nio.file.Path;
import java.util.Map;

/**
 * Outcome of a merge: where the output went, how many rows were written, and
 * how many rows each input contributed.
 */
public record MergeResult(Path output, int totalRows, Map<String, Integer> rowsPerFile) {

    public String report() {
        StringBuilder sb = new StringBuilder("Merge report\n");
        rowsPerFile.forEach((file, count) -> sb.append("  ").append(file).append(": ").append(count).append(" rows\n"));
        sb.append("  total: ").append(totalRows).append(" rows");
        return sb.toString();
    }
}
