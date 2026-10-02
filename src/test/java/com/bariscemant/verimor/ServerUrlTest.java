package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.SmsClientOptions;
import com.bariscemant.verimor.support.LoopbackServer;
import java.net.URI;
import org.junit.jupiter.api.Test;

class ServerUrlTest {
    @Test
    void aCustomServerUrlKeepsItsPathPrefix() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "1", "text/plain")) {
            String base = server.uri().toString().replaceAll("/+$", "");
            new SmsClient(new SmsClientOptions("u", "p").baseUri(URI.create(base + "/verimor"))).balance();
            assertEquals("/verimor/v2/balance", server.requests().get(0).path);
        }
    }
}
