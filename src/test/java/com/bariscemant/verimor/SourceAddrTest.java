package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bariscemant.verimor.internal.VerimorJson;
import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.SmsClientOptions;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.Target;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SourceAddrTest {
    private static SmsClient client(LoopbackServer server, String sender) {
        return new SmsClient(new SmsClientOptions(Target.SMS_USER, Target.SMS_PASSWORD)
                .defaultSender(sender).baseUri(server.uri()));
    }

    private static JsonNode body(LoopbackServer server) throws Exception {
        return VerimorJson.MAPPER.readTree(server.requests().get(0).body);
    }

    @Test
    void sendUsesTheWireFieldNamesAndTheClientCredentials() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            client(server, "DEFAULT").send("905551112233", "hello");
            assertEquals("POST", server.requests().get(0).method);
            assertEquals("/v2/send.json", server.requests().get(0).path);
            JsonNode root = body(server);
            assertEquals(Target.SMS_USER, root.get("username").asText());
            assertEquals(Target.SMS_PASSWORD, root.get("password").asText());
            assertEquals("DEFAULT", root.get("source_addr").asText());
            JsonNode message = root.get("messages").get(0);
            assertEquals("905551112233", message.get("dest").asText());
            assertEquals("hello", message.get("msg").asText());
            List<String> names = new ArrayList<>();
            root.fieldNames().forEachRemaining(names::add);
            names.sort(null);
            assertEquals(List.of("messages", "password", "source_addr", "username"), names);
        }
    }

    @Test
    void perCallSenderBeatsTheClientDefault() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            client(server, "DEFAULT").send("905551112233", "hello", "CALL");
            assertEquals("CALL", body(server).get("source_addr").asText());
        }
    }

    @Test
    void sourceAddrIsOmittedWhenNeitherSideSetsIt() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            client(server, null).send("905551112233", "hello");
            assertFalse(body(server).has("source_addr"));
        }
    }

    @Test
    void legacySendPutsTheDefaultSenderInTheQuery() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            client(server, "DEFAULT").campaigns().sendLegacy("905551112233", "hello");
            assertTrue(server.requests().get(0).query.contains("source_addr=DEFAULT"));
        }
    }
}
