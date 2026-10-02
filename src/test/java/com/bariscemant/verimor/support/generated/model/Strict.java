package com.bariscemant.verimor.support.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Mirrors a generated model: a required property marked by a @Nonnull getter. */
public class Strict {
    @JsonProperty("id")
    private String id;

    @JsonProperty("note")
    private String note;

    @Nonnull
    public String getId() {
        return id;
    }

    @Nullable
    public String getNote() {
        return note;
    }
}
