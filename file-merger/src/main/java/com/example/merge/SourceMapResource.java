package com.example.merge;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Path("/source-map")
@Produces(MediaType.APPLICATION_JSON)
public class SourceMapResource {

    @Inject
    SourceMapService sourceMapService;

    /**
     * Returns {"customer": [rows...], "order": [rows...]} built from the merged CSV.
     * Responds 404 if the merged file has not been generated yet.
     */
    @GET
    public Map<String, List<SourceRow>> get() throws IOException {
        return sourceMapService.sourceMap();
    }
}
