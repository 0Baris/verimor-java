package com.bariscemant.verimor.support;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** A loopback-only HTTP server so no test can reach a real endpoint. */
public final class LoopbackServer implements AutoCloseable {
    public static final class Recorded {
        public final String method;
        public final String path;
        public final String query;
        public final Map<String, String> headers;
        public final String body;

        Recorded(String method, String path, String query, Map<String, String> headers, String body) {
            this.method = method;
            this.path = path;
            this.query = query;
            this.headers = headers;
            this.body = body;
        }
    }

    private final HttpServer server;
    private final List<Recorded> requests = Collections.synchronizedList(new ArrayList<>());

    private LoopbackServer(int status, byte[] body, String contentType, Duration delay) throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            byte[] received;
            try (InputStream input = exchange.getRequestBody()) {
                received = input.readAllBytes();
            }
            Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            exchange.getRequestHeaders().forEach((name, values) -> headers.put(name, String.join(",", values)));
            URI uri = exchange.getRequestURI();
            requests.add(new Recorded(
                    exchange.getRequestMethod(),
                    uri.getRawPath(),
                    uri.getRawQuery() == null ? "" : uri.getRawQuery(),
                    headers,
                    new String(received, StandardCharsets.UTF_8)));
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
            try {
                exchange.getResponseHeaders().add("Content-Type", contentType);
                exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
                if (body.length > 0) {
                    try (OutputStream output = exchange.getResponseBody()) {
                        output.write(body);
                    }
                }
            } catch (IOException clientGone) {
                // The client may have timed out already.
            } finally {
                exchange.close();
            }
        });
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
    }

    public static LoopbackServer respond(int status, String body, String contentType) throws IOException {
        return new LoopbackServer(status, body.getBytes(StandardCharsets.UTF_8), contentType, Duration.ZERO);
    }

    public static LoopbackServer respond(int status, String body) throws IOException {
        return respond(status, body, "application/json");
    }

    public static LoopbackServer delayed(Duration delay) throws IOException {
        return new LoopbackServer(200, "{}".getBytes(StandardCharsets.UTF_8), "application/json", delay);
    }

    public static LoopbackServer bytes(int status, byte[] body, String contentType) throws IOException {
        return new LoopbackServer(status, body, contentType, Duration.ZERO);
    }

    public URI uri() {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
    }

    public List<Recorded> requests() {
        synchronized (requests) {
            return new ArrayList<>(requests);
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
