package com.example.merge;

import io.smallrye.config.ConfigMapping;

import java.nio.file.Path;
import java.util.List;

@ConfigMapping(prefix = "merge")
public interface MergeConfig {

    /** The input CSV files to merge, in order. */
    List<String> inputFiles();

    /** Folder where the merged output is written. */
    Path outputDir();

    /** Name of the merged output file inside {@link #outputDir()}. */
    String outputFile();
}
