package com.example.merge;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import java.nio.file.Path;
import java.util.List;

/**
 * Entry point: merges the configured input files into the results folder, then keeps
 * the HTTP server running so the source-map API can serve the merged output.
 */
@QuarkusMain
public class FileMergerApp implements QuarkusApplication {

    @Inject
    MergeService mergeService;

    @Inject
    MergeConfig config;

    @Override
    public int run(String... args) throws Exception {
        List<Path> inputs = config.inputFiles().stream().map(Path::of).toList();
        Path output = config.outputDir().resolve(config.outputFile());

        MergeResult result = mergeService.merge(inputs, output);

        System.out.println(result.report());
        System.out.println("Merged output written to " + output.toAbsolutePath());
        System.out.println("Source map API: GET http://localhost:8080/source-map");
        Quarkus.waitForExit();
        return 0;
    }

    public static void main(String... args) {
        Quarkus.run(FileMergerApp.class, args);
    }
}
