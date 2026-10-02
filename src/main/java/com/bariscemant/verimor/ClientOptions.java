package com.bariscemant.verimor;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.Nullable;

/** Options shared by every product client. */
public abstract class ClientOptions<T extends ClientOptions<T>> {
    private @Nullable URI baseUri;
    private Duration timeout = Duration.ofSeconds(30);
    private @Nullable HttpClient httpClient;

    protected abstract T self();

    /** Overrides the product's default address (for example a local test server). */
    public T baseUri(URI baseUri) {
        this.baseUri = Objects.requireNonNull(baseUri, "baseUri");
        return self();
    }

    /** Per-request timeout; 30 seconds by default. The SDK never retries. */
    public T timeout(Duration timeout) {
        Objects.requireNonNull(timeout, "timeout");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        this.timeout = timeout;
        return self();
    }

    /** A caller-owned client; the SDK never closes or reconfigures it. */
    public T httpClient(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        return self();
    }

    public @Nullable URI baseUri() {
        return baseUri;
    }

    public Duration timeout() {
        return timeout;
    }

    public @Nullable HttpClient httpClient() {
        return httpClient;
    }

    /** Rejects blank values without echoing any secret. */
    protected static String require(@Nullable String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value;
    }
}
