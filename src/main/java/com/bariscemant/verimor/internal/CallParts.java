package com.bariscemant.verimor.internal;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/** The caller-supplied pieces of one operation call; values are formatted locale-independently. */
public final class CallParts {
    private final Map<String, String> path = new LinkedHashMap<>();
    private final Map<String, String> query = new LinkedHashMap<>();
    private final Map<String, String> headers = new LinkedHashMap<>();
    private @Nullable Map<String, String> form;
    private @Nullable String jsonBody;

    public CallParts path(String name, Object value) {
        path.put(name, format(value));
        return this;
    }

    public CallParts query(String name, @Nullable Object value) {
        if (value != null) {
            query.put(name, format(value));
        }
        return this;
    }

    public CallParts header(String name, @Nullable Object value) {
        if (value != null) {
            headers.put(name, format(value));
        }
        return this;
    }

    public CallParts form(String name, @Nullable Object value) {
        if (value != null) {
            if (form == null) {
                form = new LinkedHashMap<>();
            }
            form.put(name, format(value));
        }
        return this;
    }

    public CallParts jsonBody(@Nullable String jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }

    public Map<String, String> path() {
        return path;
    }

    public Map<String, String> query() {
        return query;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public @Nullable Map<String, String> form() {
        return form;
    }

    public @Nullable String jsonBody() {
        return jsonBody;
    }

    static String format(Object value) {
        if (value instanceof Collection) {
            return ((Collection<?>) value).stream().map(CallParts::format).collect(Collectors.joining(","));
        }
        // Boolean, Number and String all format without locale (Double.toString is locale-independent).
        return String.valueOf(value);
    }
}
