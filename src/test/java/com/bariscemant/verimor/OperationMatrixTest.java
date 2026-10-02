package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bariscemant.verimor.internal.VerimorJson;
import com.bariscemant.verimor.support.Contract;
import com.bariscemant.verimor.support.LoopbackServer;
import com.bariscemant.verimor.support.Samples;
import com.bariscemant.verimor.support.Target;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OperationMatrixTest {
    static List<Contract.Operation> operations() throws Exception {
        return Contract.load();
    }

    @Test
    void theContractListsAll72Operations() throws Exception {
        List<Contract.Operation> operations = Contract.load();
        assertEquals(72, operations.size());
        Map<String, Long> byProduct = operations.stream()
                .collect(Collectors.groupingBy(o -> o.product, Collectors.counting()));
        assertEquals(Map.of("sms", 14L, "switch", 52L, "whatsapp", 6L), byProduct);
    }

    @ParameterizedTest
    @MethodSource("operations")
    void everyOperationReachesItsEndpointWithItsCredentials(Contract.Operation operation) throws Exception {
        String[] proxy = operation.proxy.split("\\.");
        Object probe = Target.client(operation.product, java.net.URI.create("http://127.0.0.1:1/"));
        Method serviceGetter = probe.getClass().getMethod(proxy[0]);
        Method method = Arrays.stream(serviceGetter.getReturnType().getMethods())
                .filter(m -> m.getName().equals(proxy[1]))
                .max(Comparator.comparingInt(Method::getParameterCount))
                .orElseThrow();

        try (LoopbackServer server = respondFor(method.getGenericReturnType())) {
            Object client = Target.client(operation.product, server.uri());
            Object service = serviceGetter.invoke(client);
            Object[] arguments = Arrays.stream(method.getGenericParameterTypes()).map(Samples::create).toArray();
            method.invoke(service, arguments);

            assertEquals(1, server.requests().size());
            LoopbackServer.Recorded seen = server.requests().get(0);
            assertEquals(operation.method, seen.method);
            String pattern = "^" + Arrays.stream(operation.path.split("\\{[^}]+\\}", -1))
                    .map(Pattern::quote).collect(Collectors.joining("[^/]+")) + "$";
            assertTrue(seen.path.matches(pattern), seen.path + " !~ " + operation.path);

            RawOperation raw = Target.raw(client).operations().stream()
                    .filter(o -> o.operationId().equals(operation.operationId)).findFirst().orElseThrow();
            assertEquals(operation.method, raw.method());
            assertEquals(operation.path, raw.pathTemplate());
            for (Map.Entry<String, String> credential : raw.credentialLocations().entrySet()) {
                switch (credential.getValue()) {
                    case "query":
                        assertTrue(seen.query.contains(credential.getKey() + "="), seen.query);
                        break;
                    case "header":
                        assertTrue(seen.headers.containsKey(credential.getKey()));
                        break;
                    default:
                        JsonNode body = VerimorJson.MAPPER.readTree(seen.body);
                        assertFalse(body.get(credential.getKey()).asText().isEmpty());
                }
            }
        }
    }

    @Test
    void credentialWiringMatchesTheContractCounts() {
        assertEquals(10, count("sms", "query"));
        assertEquals(4, count("sms", "body"));
        assertEquals(50, count("switch", "query"));
        assertEquals(5, count("whatsapp", "header"));
    }

    private static long count(String product, String location) {
        RawClient raw = Target.raw(Target.client(product, java.net.URI.create("http://127.0.0.1:1/")));
        return raw.operations().stream().filter(o -> o.credentialLocations().containsValue(location)).count();
    }

    private static LoopbackServer respondFor(Type result) throws Exception {
        if (result == void.class) return LoopbackServer.respond(200, "", "text/plain");
        if (result == String.class) return LoopbackServer.respond(200, "ok", "text/plain");
        if (result == byte[].class) return LoopbackServer.bytes(200, new byte[] {1, 2, 3}, "application/pdf");
        if (result == JsonNode.class) return LoopbackServer.respond(200, "{}");
        return LoopbackServer.respond(200, VerimorJson.serialize(Samples.create(result)));
    }
}
