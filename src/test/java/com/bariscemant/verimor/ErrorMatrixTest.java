package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.Target;
import com.bariscemant.verimor.switchapi.SwitchClient;
import com.bariscemant.verimor.whatsapp.WhatsAppClient;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ErrorMatrixTest {
    private static final int[] STATUSES = {400, 401, 403, 404, 422, 500, 503};

    static Stream<Arguments> cases() {
        List<Arguments> cases = new ArrayList<>();
        for (String product : new String[] {"sms", "switch", "whatsapp"}) {
            for (int status : STATUSES) {
                for (ErrorBodyKind kind : ErrorBodyKind.values()) {
                    cases.add(Arguments.of(product, status, kind));
                }
            }
        }
        return cases.stream();
    }

    @Test
    void theMatrixHas84Cases() {
        assertEquals(84, cases().count());
    }

    @ParameterizedTest
    @MethodSource("cases")
    void everyNon2xxResponseBecomesAVerimorApiException(String product, int status, ErrorBodyKind kind) throws Exception {
        try (LoopbackServer server = respond(status, kind)) {
            VerimorApiException error = assertThrows(VerimorApiException.class, call(product, server.uri()));
            assertEquals(status, error.statusCode());
            assertEquals(kind, error.bodyKind());
            assertTrue(error.getMessage().contains(Integer.toString(status)));
            for (String secret : new String[] {Target.SMS_USER, Target.SMS_PASSWORD, Target.SWITCH_KEY, Target.WHATSAPP_KEY}) {
                assertFalse(error.getMessage().contains(secret));
            }
            assertEquals(1, server.requests().size());
        }
    }

    @Test
    void aJsonErrorMessageFieldIsSurfaced() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(422, "{\"message\":\"invalid destination\"}")) {
            VerimorApiException error = assertThrows(VerimorApiException.class, call("sms", server.uri()));
            assertTrue(error.getMessage().contains("invalid destination"));
        }
    }

    private static LoopbackServer respond(int status, ErrorBodyKind kind) throws Exception {
        switch (kind) {
            case JSON:
                return LoopbackServer.respond(status, "{\"message\":\"rejected\"}");
            case TEXT:
                return LoopbackServer.respond(status, "rejected", "text/plain");
            case EMPTY:
                return LoopbackServer.respond(status, "", "text/plain");
            default:
                return LoopbackServer.bytes(status, new byte[] {0x00, (byte) 0xFF, 0x10}, "application/octet-stream");
        }
    }

    private static Executable call(String product, URI uri) {
        Object client = Target.client(product, uri);
        switch (product) {
            case "sms":
                return () -> ((SmsClient) client).balance();
            case "switch":
                return () -> ((SwitchClient) client).calls().hangup("1");
            default:
                return () -> ((WhatsAppClient) client).health().health();
        }
    }
}
