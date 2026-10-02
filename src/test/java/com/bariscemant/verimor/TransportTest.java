package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bariscemant.verimor.internal.Transport;
import com.bariscemant.verimor.internal.TransportRequest;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.TestOptions;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TransportTest {
    @Test
    void defaultTimeoutIsThirtySeconds() {
        assertEquals(Duration.ofSeconds(30), new TestOptions().timeout());
    }

    @Test
    void rejectsNonPositiveTimeouts() {
        assertThrows(IllegalArgumentException.class, () -> new TestOptions().timeout(Duration.ZERO));
    }

    @Test
    void anInjectedClientIsUsedAndLeftUsable() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "{}")) {
            HttpClient client = HttpClient.newHttpClient();
            Transport transport = new Transport(new TestOptions().httpClient(client), server.uri());
            transport.send(TransportRequest.get("/v2/balance"));
            transport.send(TransportRequest.get("/v2/balance"));
            assertEquals(2, server.requests().size());
        }
    }

    @Test
    void aServerErrorIsSentExactlyOnce() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(503, "unavailable", "text/plain")) {
            Transport transport = new Transport(new TestOptions(), server.uri());
            VerimorApiException error = assertThrows(
                    VerimorApiException.class, () -> transport.send(TransportRequest.get("/v2/balance")));
            assertEquals(503, error.statusCode());
            assertEquals(1, server.requests().size());
        }
    }

    @Test
    void aTimeoutIsNativeAndNotRetried() throws Exception {
        // The timeout leaves the request time to arrive even on a slow runner; the delay outlasts it.
        try (LoopbackServer server = LoopbackServer.delayed(Duration.ofSeconds(8))) {
            Transport transport = new Transport(new TestOptions().timeout(Duration.ofSeconds(2)), server.uri());
            assertThrows(HttpTimeoutException.class, () -> transport.send(TransportRequest.get("/v2/balance")));
            assertEquals(1, server.requests().size());
        }
    }

    @Test
    void sendsQueryHeadersAndJsonBody() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "{\"ok\":true}")) {
            Transport transport = new Transport(new TestOptions(), server.uri());
            transport.send(new TransportRequest(
                    "POST", "/v2/send.json", Map.of("a b", "c&d"), Map.of("x-api-key", "k"), "{\"x\":1}", null));
            LoopbackServer.Recorded seen = server.requests().get(0);
            assertEquals("POST", seen.method);
            assertEquals("/v2/send.json", seen.path);
            assertEquals("a%20b=c%26d", seen.query);
            assertEquals("k", seen.headers.get("x-api-key"));
            assertEquals("{\"x\":1}", seen.body);
        }
    }

    @Test
    void patchIsSupported() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            Transport transport = new Transport(new TestOptions(), server.uri());
            transport.send(new TransportRequest("PATCH", "/contacts/1", Map.of(), Map.of(), null, Map.of("name", "a")));
            assertEquals("PATCH", server.requests().get(0).method);
        }
    }
}
