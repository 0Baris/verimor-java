package com.bariscemant.verimor.support;

import com.bariscemant.verimor.internal.VerimorJson;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** The exported 72-operation contract, read from the repository root. */
public final class Contract {
    public static final class Operation {
        public final String product;
        public final String operationId;
        public final String method;
        public final String path;
        public final String proxy;

        Operation(JsonNode node) {
            product = node.get("product").asText();
            operationId = node.get("operationId").asText();
            method = node.get("method").asText();
            path = node.get("path").asText();
            proxy = node.get("proxy").asText();
        }

        @Override
        public String toString() {
            return product + " " + operationId;
        }
    }

    private Contract() {
    }

    public static List<Operation> load() throws IOException {
        List<Operation> operations = new ArrayList<>();
        for (JsonNode node : VerimorJson.MAPPER.readTree(Files.readString(Path.of("contracts/operations.json")))) {
            operations.add(new Operation(node));
        }
        return operations;
    }
}
