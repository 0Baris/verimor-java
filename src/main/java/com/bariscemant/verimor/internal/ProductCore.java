package com.bariscemant.verimor.internal;

import com.bariscemant.verimor.RawOperation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Everything product-specific before a request is sent: path expansion, credential placement and
 * the default SMS sender. Holds this client's credentials only.
 */
public final class ProductCore {
    private static final String SENDER_FIELD = "source_addr";

    private final Transport transport;
    private final Map<String, String> credentials;
    private final @Nullable String defaultSender;

    public ProductCore(Transport transport, Map<String, String> credentials, @Nullable String defaultSender) {
        this.transport = transport;
        this.credentials = Collections.unmodifiableMap(new LinkedHashMap<>(credentials));
        this.defaultSender = defaultSender;
    }

    public TransportResponse send(RawOperation operation, CallParts call) throws IOException, InterruptedException {
        Map<String, String> query = new LinkedHashMap<>(call.query());
        Map<String, String> headers = new LinkedHashMap<>(call.headers());
        ObjectNode body = null;

        for (Map.Entry<String, String> credential : operation.credentialLocations().entrySet()) {
            String value = credentials.get(credential.getKey());
            if (value == null) {
                continue;
            }
            switch (credential.getValue()) {
                case "query":
                    query.putIfAbsent(credential.getKey(), value);
                    break;
                case "header":
                    headers.putIfAbsent(credential.getKey(), value);
                    break;
                case "body":
                    body = body(body, call);
                    if (missingOrEmpty(body, credential.getKey())) {
                        body.put(credential.getKey(), value);
                    }
                    break;
                default:
                    break;
            }
        }

        if (defaultSender != null && operation.senderLocation() != null) {
            if ("query".equals(operation.senderLocation())) {
                query.putIfAbsent(SENDER_FIELD, defaultSender);
            } else {
                body = body(body, call);
                if (!body.has(SENDER_FIELD) || body.get(SENDER_FIELD).isNull()) {
                    body.put(SENDER_FIELD, defaultSender);
                }
            }
        }

        TransportRequest request = new TransportRequest(
                operation.method(),
                expand(operation.pathTemplate(), call.path()),
                query,
                headers,
                body != null ? VerimorJson.serialize(body) : call.jsonBody(),
                call.form());
        return transport.send(request);
    }

    private static ObjectNode body(@Nullable ObjectNode current, CallParts call) {
        if (current != null) {
            return current;
        }
        if (call.jsonBody() == null) {
            return VerimorJson.MAPPER.createObjectNode();
        }
        JsonNode parsed = ErrorBodies.parse(call.jsonBody());
        if (!(parsed instanceof ObjectNode)) {
            throw new IllegalArgumentException("The request body must be a JSON object.");
        }
        return (ObjectNode) parsed;
    }

    // Generated SMS request models require username/password; callers pass "" and the client fills them in.
    private static boolean missingOrEmpty(ObjectNode body, String key) {
        JsonNode node = body.get(key);
        return node == null || node.isNull() || (node.isTextual() && node.asText().isEmpty());
    }

    private static String expand(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> value : values.entrySet()) {
            String escaped = URLEncoder.encode(value.getValue(), StandardCharsets.UTF_8).replace("+", "%20");
            result = result.replace("{" + value.getKey() + "}", escaped);
        }
        if (result.indexOf('{') >= 0) {
            throw new IllegalArgumentException("A path value is missing for " + template + ".");
        }
        return result;
    }
}
