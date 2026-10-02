package com.bariscemant.verimor.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class VerimorJson {
    /** Unknown response fields are ignored so new API fields do not break older SDKs; nulls are not sent. */
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    private VerimorJson() {
    }

    public static String serialize(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("The request body cannot be written as JSON.", exception);
        }
    }
}
