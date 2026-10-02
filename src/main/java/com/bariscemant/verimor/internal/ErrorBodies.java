package com.bariscemant.verimor.internal;

import com.bariscemant.verimor.ErrorBodyKind;
import com.bariscemant.verimor.VerimorApiException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ErrorBodies {
    private static final int MAX_SUMMARY = 200;
    private static final String[] MESSAGE_FIELDS = {"message", "detail", "error", "msg"};

    private ErrorBodies() {
    }

    public static ErrorBodyKind classify(byte[] content, @Nullable String contentType) {
        if (content.length == 0) {
            return ErrorBodyKind.EMPTY;
        }
        String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (type.startsWith("application/octet-stream") || type.startsWith("image/") || type.startsWith("audio/")
                || type.startsWith("video/") || type.startsWith("application/pdf") || type.startsWith("application/zip")) {
            return ErrorBodyKind.BINARY;
        }
        String text;
        try {
            text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(content))
                    .toString();
        } catch (CharacterCodingException exception) {
            return ErrorBodyKind.BINARY;
        }
        return parse(text) != null ? ErrorBodyKind.JSON : ErrorBodyKind.TEXT;
    }

    public static VerimorApiException toException(TransportResponse response) {
        ErrorBodyKind kind = classify(response.content(), response.contentType());
        String body = kind == ErrorBodyKind.BINARY ? "" : response.body();
        String summary = kind == ErrorBodyKind.JSON ? jsonSummary(body) : truncate(body.trim());
        String message = summary.isEmpty()
                ? "Verimor API returned HTTP " + response.statusCode()
                : "Verimor API returned HTTP " + response.statusCode() + ": " + summary;
        return new VerimorApiException(response.statusCode(), kind, body, message);
    }

    static @Nullable JsonNode parse(String text) {
        try {
            return VerimorJson.MAPPER.readTree(text);
        } catch (IOException exception) {
            return null;
        }
    }

    private static String jsonSummary(String body) {
        JsonNode node = parse(body);
        if (node != null && node.isObject()) {
            for (String field : MESSAGE_FIELDS) {
                JsonNode value = node.get(field);
                if (value != null && value.isTextual()) {
                    return truncate(value.asText());
                }
            }
        }
        return truncate(body.trim());
    }

    private static String truncate(String value) {
        return value.length() <= MAX_SUMMARY ? value : value.substring(0, MAX_SUMMARY);
    }
}
