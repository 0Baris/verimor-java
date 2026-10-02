package com.bariscemant.verimor.internal;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.Nullable;

public final class TransportResponse {
    private final int statusCode;
    private final byte[] content;
    private final @Nullable String contentType;
    private final Map<String, String> headers;

    public TransportResponse(int statusCode, byte[] content, @Nullable String contentType, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.content = content;
        this.contentType = contentType;
        this.headers = headers;
    }

    public int statusCode() {
        return statusCode;
    }

    public byte[] content() {
        return content;
    }

    public @Nullable String contentType() {
        return contentType;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String body() {
        return new String(content, StandardCharsets.UTF_8);
    }
}
