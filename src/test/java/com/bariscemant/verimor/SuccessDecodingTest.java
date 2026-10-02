package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.Target;
import com.bariscemant.verimor.whatsapp.WhatsAppClient;
import com.bariscemant.verimor.whatsapp.generated.model.MessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SuccessDecodingTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "not json", "{\"a\":1}", "null"})
    void aMalformed2xxListBodyIsRejected(String body) throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, body)) {
            assertThrows(UnexpectedResponseException.class, () -> Target.sms(server.uri()).statusById(1));
        }
    }

    @Test
    void aValid202WhatsAppBodyIsDecoded() throws Exception {
        String body = "{\"id\":\"3f2504e0-4f89-41d3-9a0c-0305e82c3301\",\"status\":\"queued\"}";
        try (LoopbackServer server = LoopbackServer.respond(202, body)) {
            WhatsAppClient client = (WhatsAppClient) Target.client("whatsapp", server.uri());
            MessageResponse result = client.sendOtp("905551112233", "otp", null, null);
            assertEquals("queued", result.getStatus());
            assertEquals("3f2504e0-4f89-41d3-9a0c-0305e82c3301", result.getId().toString());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"status\":\"queued\"}", "accepted", ""})
    void anInvalid202WhatsAppBodyIsRejected(String body) throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(202, body)) {
            WhatsAppClient client = (WhatsAppClient) Target.client("whatsapp", server.uri());
            assertThrows(UnexpectedResponseException.class, () -> client.sendOtp("905551112233", "otp", null, null));
        }
    }

    @Test
    void aTextBalanceIsReturnedVerbatim() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "123.45", "text/plain")) {
            assertEquals("123.45", Target.sms(server.uri()).balance());
        }
    }
}
