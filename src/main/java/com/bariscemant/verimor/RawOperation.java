package com.bariscemant.verimor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

/** One Verimor operation: its id, HTTP method, path template and credential wiring. */
public final class RawOperation {
    private final String operationId;
    private final String method;
    private final String pathTemplate;
    private final Map<String, String> credentialLocations;
    private final @Nullable String senderLocation;

    public RawOperation(
            String operationId,
            String method,
            String pathTemplate,
            Map<String, String> credentialLocations,
            @Nullable String senderLocation) {
        this.operationId = operationId;
        this.method = method;
        this.pathTemplate = pathTemplate;
        this.credentialLocations = Collections.unmodifiableMap(new LinkedHashMap<>(credentialLocations));
        this.senderLocation = senderLocation;
    }

    public String operationId() {
        return operationId;
    }

    public String method() {
        return method;
    }

    public String pathTemplate() {
        return pathTemplate;
    }

    /** Credential field name to where it is sent: "query", "header" or "body". */
    public Map<String, String> credentialLocations() {
        return credentialLocations;
    }

    /** Where {@code source_addr} goes ("query" or "body"); null when the operation has no sender. */
    public @Nullable String senderLocation() {
        return senderLocation;
    }
}
