package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bariscemant.verimor.internal.ResponseReader;
import com.bariscemant.verimor.internal.TransportResponse;
import com.bariscemant.verimor.support.generated.model.Strict;
import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

class ResponseReaderTest {
    private static TransportResponse response(String body, String type) {
        return new TransportResponse(200, body.getBytes(StandardCharsets.UTF_8), type, Map.of());
    }

    @Test
    void readsATypedJsonBodyAndIgnoresUnknownFields() {
        Strict value = ResponseReader.json(response("{\"id\":\"a\",\"extra\":1}", "application/json"), Strict.class);
        assertEquals("a", value.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "null", "not json", "[1,2]", "{}", "{\"note\":\"x\"}"})
    void rejectsEmptyNonJsonWrongShapedOrIncompleteBodies(String body) {
        assertThrows(UnexpectedResponseException.class,
                () -> ResponseReader.json(response(body, "application/json"), Strict.class));
    }

    @Test
    void checksEveryListItem() {
        assertThrows(UnexpectedResponseException.class, () -> ResponseReader.json(
                response("[{\"id\":\"a\"},{}]", "application/json"), new TypeReference<List<Strict>>() { }));
    }

    @Test
    void readsTextBytesAndElements() {
        assertEquals("12.5", ResponseReader.text(response("12.5", "text/plain")));
        assertArrayEquals(new byte[] {0x31, 0x32}, ResponseReader.bytes(response("12", "application/pdf")));
        assertEquals(1, ResponseReader.element(response("{\"a\":1}", "application/json")).get("a").asInt());
        assertThrows(UnexpectedResponseException.class, () -> ResponseReader.element(response("nope", "text/plain")));
    }
}
