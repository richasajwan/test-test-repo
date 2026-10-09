package com.example.merge;

import java.nio.file.Path;

/** Thrown when the merged CSV has not been produced yet. */
public class MergedFileMissingException extends RuntimeException {

    public MergedFileMissingException(Path merged) {
        super("Merged file not found: " + merged.toAbsolutePath() + ". Run the merge first.");
Test replacement suggestion by Richa
}
