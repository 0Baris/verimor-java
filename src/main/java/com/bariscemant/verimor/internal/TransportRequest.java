package com.bariscemant.verimor.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class TransportRequest {
    private final String method;
    private final String path;
    private final Map<String, String> query;
    private final Map<String, String> headers;
    private final @Nullable String jsonBody;
    private final @Nullable Map<String, String> formBody;

    public TransportRequest(
            String method,
            String path,
            Map<String, String> query,
            Map<String, String> headers,
            @Nullable String jsonBody,
            @Nullable Map<String, String> formBody) {
        this.method = method;
        this.path = path;
        this.query = Collections.unmodifiableMap(new LinkedHashMap<>(query));
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.jsonBody = jsonBody;
        this.formBody = formBody == null ? null : Collections.unmodifiableMap(new LinkedHashMap<>(formBody));
    }

    public static TransportRequest get(String path) {
        return new TransportRequest("GET", path, Collections.emptyMap(), Collections.emptyMap(), null, null);
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public Map<String, String> query() {
        return query;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public @Nullable String jsonBody() {
        return jsonBody;
    }

    public @Nullable Map<String, String> formBody() {
        return formBody;
    }
}
