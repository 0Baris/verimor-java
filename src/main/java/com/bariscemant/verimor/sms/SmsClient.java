package com.bariscemant.verimor.sms;

import com.bariscemant.verimor.internal.ProductCore;
import com.bariscemant.verimor.internal.Transport;
import com.bariscemant.verimor.sms.generated.model.GetSmsStatus200ResponseInner;
import com.bariscemant.verimor.sms.generated.model.SendSmsJsonRequest;
import com.bariscemant.verimor.sms.generated.model.SendSmsJsonRequestMessagesInner;
import com.bariscemant.verimor.sms.service.RawOperations;
import com.bariscemant.verimor.sms.service.SmsClientBase;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

/** Verimor SMS client. Services cover every operation; the methods below are shortcuts. */
public final class SmsClient extends SmsClientBase {
    public SmsClient(SmsClientOptions options) {
        super(core(options));
    }

    private static ProductCore core(SmsClientOptions options) {
        Objects.requireNonNull(options, "options");
        return new ProductCore(
                new Transport(options, RawOperations.DEFAULT_BASE_URI), options.credentials(), options.defaultSender());
    }

    /** Sends one message using the default sender. */
    public String send(String destination, String message) throws IOException, InterruptedException {
        return send(destination, message, null);
    }

    /** Sends one message; {@code sourceAddr} overrides the default sender for this call. */
    public String send(String destination, String message, @Nullable String sourceAddr)
            throws IOException, InterruptedException {
        SendSmsJsonRequest request = new SendSmsJsonRequest()
                .username("")
                .password("")
                .messages(List.of(new SendSmsJsonRequestMessagesInner().dest(destination).msg(message)))
                .sourceAddr(sourceAddr);
        return campaigns().send(request);
    }

    /** Sends a prepared request. Pass empty strings for its username and password. */
    public String send(SendSmsJsonRequest request) throws IOException, InterruptedException {
        return campaigns().send(request);
    }

    public String balance() throws IOException, InterruptedException {
        return balances().balance();
    }

    public List<GetSmsStatus200ResponseInner> statusById(long id) throws IOException, InterruptedException {
        return reports().status(id, null, null, null);
    }

    public List<GetSmsStatus200ResponseInner> statusByCustomId(String customId) throws IOException, InterruptedException {
        return reports().status(null, null, null, Objects.requireNonNull(customId, "customId"));
    }
}
