package com.bariscemant.verimor.internal;

import com.bariscemant.verimor.UnexpectedResponseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.annotation.Nonnull;

/** Turns a 2xx response into the operation's declared result, or fails loudly. */
public final class ResponseReader {
    private ResponseReader() {
    }

    public static String text(TransportResponse response) {
        return response.body();
    }

    public static byte[] bytes(TransportResponse response) {
        return response.content().clone();
    }

    public static JsonNode element(TransportResponse response) {
        JsonNode node = ErrorBodies.parse(response.body());
        if (node == null || node.isMissingNode()) {
            throw new UnexpectedResponseException("Verimor API returned a body that is not JSON.");
        }
        return node;
    }

    public static <T> T json(TransportResponse response, Class<T> type) {
        return json(response, VerimorJson.MAPPER.getTypeFactory().constructType(type));
    }

    public static <T> T json(TransportResponse response, TypeReference<T> type) {
        return json(response, VerimorJson.MAPPER.getTypeFactory().constructType(type));
    }

    private static <T> T json(TransportResponse response, JavaType type) {
        T value;
        try {
            value = VerimorJson.MAPPER.readValue(response.body(), type);
        } catch (Exception exception) {
            throw new UnexpectedResponseException("Verimor API returned a body that is not the expected JSON.", exception);
        }
        if (value == null) {
            throw new UnexpectedResponseException("Verimor API returned an empty JSON body.");
        }
        requireDeclaredFields(value);
        return value;
    }

    /** Generated models mark required properties with @Nonnull getters; Jackson does not enforce them. */
    private static void requireDeclaredFields(Object value) {
        if (value instanceof Collection) {
            for (Object item : (Collection<?>) value) {
                if (item != null) {
                    requireDeclaredFields(item);
                }
            }
            return;
        }
        if (!value.getClass().getName().contains(".generated.model.")) {
            return;
        }
        try {
            for (Method getter : value.getClass().getMethods()) {
                if (getter.getParameterCount() == 0
                        && getter.getName().startsWith("get")
                        && getter.isAnnotationPresent(Nonnull.class)
                        && getter.invoke(value) == null) {
                    throw new UnexpectedResponseException(
                            "Verimor API response is missing the required field " + getter.getName().substring(3) + ".");
                }
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new UnexpectedResponseException("Verimor API response could not be validated.", exception);
        }
    }
}
