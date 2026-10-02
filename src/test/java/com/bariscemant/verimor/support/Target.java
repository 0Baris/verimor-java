package com.bariscemant.verimor.support;

import com.bariscemant.verimor.RawClient;
import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.SmsClientOptions;
import com.bariscemant.verimor.switchapi.SwitchClient;
import com.bariscemant.verimor.switchapi.SwitchClientOptions;
import com.bariscemant.verimor.whatsapp.WhatsAppClient;
import com.bariscemant.verimor.whatsapp.WhatsAppClientOptions;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/** Builds a product client pointed at a loopback server, with known fake credentials. */
public final class Target {
    public static final String SMS_USER = "sms-user";
    public static final String SMS_PASSWORD = "sms-password";
    public static final String SWITCH_KEY = "switch-key";
    public static final String WHATSAPP_KEY = "whatsapp-key";
    public static final String DEFAULT_SENDER = "DEFSENDER";

    private Target() {
    }

    public static Object client(String product, URI uri) {
        switch (product) {
            case "sms":
                return sms(uri);
            case "switch":
                return new SwitchClient(new SwitchClientOptions(SWITCH_KEY).baseUri(uri));
            case "whatsapp":
                return new WhatsAppClient(new WhatsAppClientOptions(WHATSAPP_KEY).baseUri(uri));
            default:
                throw new IllegalArgumentException(product);
        }
    }

    public static SmsClient sms(URI uri) {
        return new SmsClient(new SmsClientOptions(SMS_USER, SMS_PASSWORD).defaultSender(DEFAULT_SENDER).baseUri(uri));
    }

    public static RawClient raw(Object client) {
        try {
            return (RawClient) client.getClass().getMethod("raw").invoke(client);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
