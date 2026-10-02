package com.bariscemant.verimor.internal;

import com.bariscemant.verimor.ClientOptions;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/** Sends one request, once. There is deliberately no retry logic. */
public final class Transport {
    private final HttpClient client;
    private final URI baseUri;
    private final Duration timeout;

    public Transport(ClientOptions<?> options, URI defaultBaseUri) {
        this.timeout = options.timeout();
        URI base = options.baseUri() != null ? options.baseUri() : defaultBaseUri;
        String text = base.toString();
        this.baseUri = URI.create(text.endsWith("/") ? text : text + "/");
        HttpClient injected = options.httpClient();
        this.client = injected != null ? injected : HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    public TransportResponse send(TransportRequest request) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(request)).timeout(timeout);
        request.headers().forEach(builder::header);
        HttpRequest.BodyPublisher body;
        if (request.formBody() != null) {
            builder.header("Content-Type", "application/x-www-form-urlencoded");
            body = HttpRequest.BodyPublishers.ofString(encode(request.formBody(), "&"), StandardCharsets.UTF_8);
        } else if (request.jsonBody() != null) {
            builder.header("Content-Type", "application/json; charset=utf-8");
            body = HttpRequest.BodyPublishers.ofString(request.jsonBody(), StandardCharsets.UTF_8);
        } else {
            body = HttpRequest.BodyPublishers.noBody();
        }
        HttpResponse<byte[]> response = client.send(
                builder.method(request.method(), body).build(), HttpResponse.BodyHandlers.ofByteArray());

        Map<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> header : response.headers().map().entrySet()) {
            headers.put(header.getKey(), String.join(", ", header.getValue()));
        }
        TransportResponse result = new TransportResponse(
                response.statusCode(),
                response.body(),
                response.headers().firstValue("content-type").orElse(null),
                headers);
        if (result.statusCode() < 200 || result.statusCode() > 299) {
            throw ErrorBodies.toException(result);
        }
        return result;
    }

    private URI uri(TransportRequest request) {
        String path = request.path().startsWith("/") ? request.path().substring(1) : request.path();
        String query = encode(new TreeMap<>(request.query()), "&");
        return URI.create(baseUri + path + (query.isEmpty() ? "" : "?" + query));
    }

    private static String encode(Map<String, String> values, String separator) {
        StringJoiner joiner = new StringJoiner(separator);
        values.forEach((key, value) -> joiner.add(escape(key) + "=" + escape(value)));
        return joiner.toString();
    }

    private static String escape(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
