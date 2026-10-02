package com.bariscemant.verimor;

import com.bariscemant.verimor.internal.CallParts;
import com.bariscemant.verimor.internal.ProductCore;
import com.bariscemant.verimor.internal.TransportResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Complete access to every operation of one product by operation id. */
public final class RawClient {
    private final ProductCore core;
    private final List<RawOperation> operations;
    private final Map<String, RawOperation> byId = new LinkedHashMap<>();

    public RawClient(ProductCore core, List<RawOperation> operations) {
        this.core = core;
        this.operations = Collections.unmodifiableList(operations);
        for (RawOperation operation : operations) {
            byId.put(operation.operationId(), operation);
        }
    }

    public List<RawOperation> operations() {
        return operations;
    }

    public RawResponse send(String operationId, RawRequest request) throws IOException, InterruptedException {
        Objects.requireNonNull(request, "request");
        RawOperation operation = byId.get(operationId);
        if (operation == null) {
            throw new IllegalArgumentException("Unknown operation id: " + operationId);
        }
        CallParts call = new CallParts();
        call.jsonBody(request.jsonBody());
        request.pathValues().forEach(call::path);
        request.query().forEach(call::query);
        request.headers().forEach(call::header);
        TransportResponse response = core.send(operation, call);
        return new RawResponse(response.statusCode(), response.content(), response.headers());
    }
}
