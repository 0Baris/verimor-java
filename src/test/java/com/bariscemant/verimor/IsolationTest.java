package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.SmsClientOptions;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.switchapi.SwitchClient;
import com.bariscemant.verimor.switchapi.SwitchClientOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class IsolationTest {
    @Test
    void twoSmsClientsInOneProcessSendTheirOwnCredentials() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "1", "text/plain")) {
            SmsClient first = new SmsClient(new SmsClientOptions("u1", "p1").baseUri(server.uri()));
            SmsClient second = new SmsClient(new SmsClientOptions("u2", "p2").baseUri(server.uri()));
            first.balance();
            second.balance();
            first.balance();
            assertTrue(server.requests().get(0).query.contains("username=u1"));
            assertTrue(server.requests().get(1).query.contains("username=u2"));
            assertFalse(server.requests().get(2).query.contains("u2"));
        }
    }

    @Test
    void aSwitchClientNeverSendsSmsCredentials() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "ok", "text/plain")) {
            new SmsClient(new SmsClientOptions("sms-only", "sms-only").baseUri(server.uri()));
            new SwitchClient(new SwitchClientOptions("k").baseUri(server.uri())).calls().hangup("1");
            LoopbackServer.Recorded seen = server.requests().get(0);
            assertFalse((seen.query + seen.body + seen.headers.values()).contains("sms-only"));
        }
    }

    @ParameterizedTest
    @CsvSource({"'',secret-password,Username is required.", "secret-user,'',Password is required.",
            "' ',secret-password,Username is required."})
    void blankCredentialsAreRejectedWithoutEchoingSecrets(String user, String password, String expected) {
        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class, () -> new SmsClientOptions(user, password));
        assertEquals(expected, error.getMessage());
    }
}
