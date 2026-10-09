package com.example.merge;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import java.nio.file.Path;
import java.util.List;

/**
 * Entry point: reads the configured input files, merges them, and writes the result
 * into the results folder. Exits with code 1 if the merge fails.
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
        return 0;
    }

    public static void main(String... args) {
        Quarkus.run(FileMergerApp.class, args);
    }
}
