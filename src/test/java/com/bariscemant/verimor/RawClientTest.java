package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bariscemant.verimor.internal.ProductCore;
import com.bariscemant.verimor.internal.Transport;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.TestOptions;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RawClientTest {
    private static RawClient raw(LoopbackServer server) {
        ProductCore core = new ProductCore(new Transport(new TestOptions(), server.uri()), Map.of(), null);
        return new RawClient(core, List.of(new RawOperation("get_thing", "GET", "/things/{id}", Map.of(), null)));
    }

    @Test
    void sendsAnyListedOperationById() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "{\"ok\":true}")) {
            RawRequest request = new RawRequest();
            request.pathValues().put("id", "9");
            request.query().put("page", "2");
            RawResponse response = raw(server).send("get_thing", request);
            assertEquals(200, response.statusCode());
            assertEquals("{\"ok\":true}", response.body());
            assertEquals("/things/9", server.requests().get(0).path);
            assertEquals("page=2", server.requests().get(0).query);
        }
    }

    @Test
    void unknownOperationIdsAreRejectedWithoutARequest() throws Exception {
        try (LoopbackServer server = LoopbackServer.respond(200, "{}")) {
            assertThrows(IllegalArgumentException.class, () -> raw(server).send("missing", new RawRequest()));
            assertEquals(0, server.requests().size());
        }
    }
}
