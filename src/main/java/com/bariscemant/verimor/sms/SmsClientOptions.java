package com.bariscemant.verimor.sms;

import com.bariscemant.verimor.ClientOptions;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

/** Options for {@link SmsClient}. */
public final class SmsClientOptions extends ClientOptions<SmsClientOptions> {
    private final String username;
    private final String password;
    private @Nullable String defaultSender;

    public SmsClientOptions(String username, String password) {
        this.username = require(username, "Username");
        this.password = require(password, "Password");
    }

    /** The sender header ({@code source_addr}) used when a call does not set one. */
    public SmsClientOptions defaultSender(@Nullable String defaultSender) {
        this.defaultSender = defaultSender;
        return this;
    }

    public @Nullable String defaultSender() {
        return defaultSender;
    }

    Map<String, String> credentials() {
        Map<String, String> credentials = new LinkedHashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        return credentials;
    }

    @Override
    protected SmsClientOptions self() {
        return this;
    }
}
