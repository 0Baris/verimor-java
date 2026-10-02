package com.bariscemant.verimor;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/** The unparsed result of a raw call. Non-2xx responses throw instead. */
public final class RawResponse {
    private final int statusCode;
    private final byte[] content;
    private final Map<String, String> headers;

    public RawResponse(int statusCode, byte[] content, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.content = content.clone();
        this.headers = headers;
    }

    public int statusCode() {
        return statusCode;
    }

    public byte[] content() {
        return content.clone();
    }

    public String body() {
        return new String(content, StandardCharsets.UTF_8);
    }

    public Map<String, String> headers() {
        return headers;
    }
}
