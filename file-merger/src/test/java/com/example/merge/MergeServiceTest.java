package com.example.merge;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class MergeServiceTest {

    @Inject
    MergeService mergeService;

    @Test
    void mergesThreeFilesWithDifferentColumns(@TempDir Path tmp) throws Exception {
        Path a = Files.writeString(tmp.resolve("a.csv"), "id,name\n1,Alice\n2,Bob\n");
        Path b = Files.writeString(tmp.resolve("b.csv"), "id,total\n10,99\n");
        Path c = Files.writeString(tmp.resolve("c.csv"), "sku,id\nX-1,7\n");
        Path out = tmp.resolve("results").resolve("merged.csv");

        MergeResult result = mergeService.merge(List.of(a, b, c), out);

        assertEquals(4, result.totalRows());
        assertEquals(List.of(
                "source_file,id,name,total,sku",
                "a.csv,1,Alice,,",
                "a.csv,2,Bob,,",
                "b.csv,10,,99,",
                "c.csv,7,,,X-1"
        ), Files.readAllLines(out));
    }

    @Test
    void rejectsRowWithWrongColumnCount(@TempDir Path tmp) throws Exception {
        Path bad = Files.writeString(tmp.resolve("bad.csv"), "id,name\n1\n");
        Path ok = Files.writeString(tmp.resolve("ok.csv"), "id\n1\n");
        Path out = tmp.resolve("merged.csv");

        assertThrows(IllegalArgumentException.class,
                () -> mergeService.merge(List.of(ok, bad), out));
    }

    @Test
    void rejectsMissingInput(@TempDir Path tmp) {
        Path missing = tmp.resolve("nope.csv");
        assertThrows(IllegalArgumentException.class,
                () -> mergeService.merge(List.of(missing), tmp.resolve("merged.csv")));
    }
}
