package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bariscemant.verimor.internal.CallParts;
import com.bariscemant.verimor.internal.ProductCore;
import com.bariscemant.verimor.internal.Transport;
import com.bariscemant.verimor.internal.VerimorJson;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.TestOptions;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductCoreTest {
    private static ProductCore core(LoopbackServer server, Map<String, String> credentials, String sender) {
        return new ProductCore(new Transport(new TestOptions(), server.uri()), credentials, sender);
    }

    private static RawOperation operation(String method, String path, Map<String, String> credentials, String sender) {
        return new RawOperation("op", method, path, credentials, sender);
    }

    private static JsonNode json(String body) throws Exception {
        return VerimorJson.MAPPER.readTree(body);
    }

    @Test
    void queryCredentialsAreAddedAndNeverOverrideCallerValues() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            ProductCore core = core(server, Map.of("key", "secret"), null);
            RawOperation op = operation("GET", "/queues", Map.of("key", "query"), null);
            core.send(op, new CallParts());
            core.send(op, new CallParts().query("key", "caller"));
            assertEquals("key=secret", server.requests().get(0).query);
            assertEquals("key=caller", server.requests().get(1).query);
        }
    }

    @Test
    void headerCredentialsUseTheHeaderName() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "{}")) {
            core(server, Map.of("x-api-key", "k1"), null)
                    .send(operation("GET", "/health", Map.of("x-api-key", "header"), null), new CallParts());
            assertEquals("k1", server.requests().get(0).headers.get("x-api-key"));
        }
    }

    @Test
    void bodyCredentialsFillEmptyPlaceholdersButKeepCallerValues() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            ProductCore core = core(server, Map.of("username", "u", "password", "p"), null);
            RawOperation op = operation("POST", "/v2/cancel/{id}", Map.of("username", "body", "password", "body"), null);
            core.send(op, new CallParts().path("id", 7L).jsonBody("{\"username\":\"\",\"password\":null,\"extra\":1}"));
            core.send(op, new CallParts().path("id", 7L).jsonBody("{\"username\":\"mine\",\"password\":\"\"}"));
            JsonNode first = json(server.requests().get(0).body);
            assertEquals("/v2/cancel/7", server.requests().get(0).path);
            assertEquals("u", first.get("username").asText());
            assertEquals("p", first.get("password").asText());
            assertEquals(1, first.get("extra").asInt());
            JsonNode second = json(server.requests().get(1).body);
            assertEquals("mine", second.get("username").asText());
            assertEquals("p", second.get("password").asText());
        }
    }

    @Test
    void defaultSenderIsUsedOnlyWhenTheCallHasNone() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            ProductCore core = core(server, Map.of(), "DEFAULT");
            RawOperation op = operation("POST", "/v2/send.json", Map.of(), "body");
            core.send(op, new CallParts().jsonBody("{}"));
            core.send(op, new CallParts().jsonBody("{\"source_addr\":\"CALL\"}"));
            assertEquals("DEFAULT", json(server.requests().get(0).body).get("source_addr").asText());
            assertEquals("CALL", json(server.requests().get(1).body).get("source_addr").asText());
        }
    }

    @Test
    void sourceAddrIsOmittedWhenNeitherSideSetsIt() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            core(server, Map.of(), null).send(
                    operation("POST", "/v2/send.json", Map.of(), "body"), new CallParts().jsonBody("{\"a\":1}"));
            assertFalse(json(server.requests().get(0).body).has("source_addr"));
        }
    }

    @Test
    void defaultSenderGoesToTheQueryForQueryOperations() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            core(server, Map.of(), "DEFAULT").send(operation("GET", "/v2/send", Map.of(), "query"), new CallParts());
            assertEquals("source_addr=DEFAULT", server.requests().get(0).query);
        }
    }

    @Test
    void pathValuesAreEscapedAndMissingValuesRejected() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            ProductCore core = core(server, Map.of(), null);
            RawOperation op = operation("GET", "/fax_document/{id}", Map.of(), null);
            core.send(op, new CallParts().path("id", "a b/c"));
            assertEquals("/fax_document/a%20b%2Fc", server.requests().get(0).path);
            assertThrows(IllegalArgumentException.class, () -> core.send(op, new CallParts()));
            assertEquals(1, server.requests().size());
        }
    }

    @Test
    void formBodiesAreUrlEncoded() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            core(server, Map.of(), null).send(
                    operation("POST", "/announcements", Map.of(), null),
                    new CallParts().form("name", "a b").form("sounddata", "x&y"));
            LoopbackServer.Recorded seen = server.requests().get(0);
            assertEquals("name=a%20b&sounddata=x%26y", seen.body);
            assertTrue(seen.headers.get("Content-Type").startsWith("application/x-www-form-urlencoded"));
        }
    }

    @Test
    void twoCoresNeverShareCredentials() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            RawOperation op = operation("GET", "/q", Map.of("key", "query"), null);
            core(server, Map.of("key", "one"), null).send(op, new CallParts());
            core(server, Map.of("key", "two"), null).send(op, new CallParts());
            assertEquals("key=one", server.requests().get(0).query);
            assertEquals("key=two", server.requests().get(1).query);
        }
    }
}
