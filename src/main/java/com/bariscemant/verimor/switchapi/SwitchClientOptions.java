package com.bariscemant.verimor.switchapi;

import com.bariscemant.verimor.ClientOptions;
import java.util.Map;

/** Options for {@link SwitchClient}. */
public final class SwitchClientOptions extends ClientOptions<SwitchClientOptions> {
    private final String key;

    public SwitchClientOptions(String key) {
        this.key = require(key, "Key");
    }

    Map<String, String> credentials() {
        return Map.of("key", key);
    }

    @Override
    protected SwitchClientOptions self() {
        return this;
    }
}
