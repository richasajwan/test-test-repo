package com.example.merge;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
class SourceMapResourceTest {

    @Inject
    MergeConfig config;

    @Test
    void returnsCustomerAndOrderRowsFromMergedCsv() throws Exception {
        // Writes the merged file the API reads (test output dir, not the real results/)
        java.nio.file.Path out = config.outputDir().resolve(config.outputFile());
        Files.createDirectories(out.getParent());
        Files.writeString(out, """
                source_file,customer_id,name,email,order_id,product_id,quantity,product_name,price
                customers.csv,C001,Alice Johnson,alice@example.com,,,,,
                orders.csv,C001,,,O100,P10,2,,
                products.csv,,,,,P10,,Keyboard,49.99
                """);

        given()
            .when().get("/source-map")
            .then()
            .statusCode(200)
            .body("customer", hasSize(1))
            .body("customer[0].source_file", is("customers.csv"))
            .body("customer[0].customer_id", is("C001"))
            .body("customer[0].order_id", nullValue())
            .body("order", hasSize(1))
            .body("order[0].order_id", is("O100"))
            .body("order[0].quantity", is("2"))
            .body("$", not(hasKey("product")));
    }
}
