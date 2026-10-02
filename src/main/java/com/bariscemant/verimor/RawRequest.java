package com.bariscemant.verimor;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

/** A request for {@link RawClient}: path values, query, headers and an optional JSON body. */
public final class RawRequest {
    private final Map<String, String> pathValues = new LinkedHashMap<>();
    private final Map<String, String> query = new LinkedHashMap<>();
    private final Map<String, String> headers = new LinkedHashMap<>();
    private @Nullable String jsonBody;

    public Map<String, String> pathValues() {
        return pathValues;
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

    public RawRequest jsonBody(@Nullable String jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }
}
