package com.bariscemant.verimor.whatsapp;

import com.bariscemant.verimor.ClientOptions;
import java.util.Map;

/** Options for {@link WhatsAppClient}. */
public final class WhatsAppClientOptions extends ClientOptions<WhatsAppClientOptions> {
    private final String apiKey;

    public WhatsAppClientOptions(String apiKey) {
        this.apiKey = require(apiKey, "ApiKey");
    }

    Map<String, String> credentials() {
        return Map.of("x-api-key", apiKey);
    }

    @Override
    protected WhatsAppClientOptions self() {
        return this;
    }
}
