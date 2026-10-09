package com.example.merge;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One row of the merged CSV. Cells that were empty in the merged file are null.
 */
public record SourceRow(
        @JsonProperty("source_file") String sourceFile,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("order_id") String orderId,
        @JsonProperty("product_id") String productId,
        @JsonProperty("quantity") String quantity,
        @JsonProperty("product_name") String productName,
        @JsonProperty("price") String price) {
}
