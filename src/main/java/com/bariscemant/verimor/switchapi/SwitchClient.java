package com.bariscemant.verimor.switchapi;

import com.bariscemant.verimor.internal.ProductCore;
import com.bariscemant.verimor.internal.Transport;
import com.bariscemant.verimor.switchapi.generated.model.OriginateCallPostRequest;
import com.bariscemant.verimor.switchapi.service.RawOperations;
import com.bariscemant.verimor.switchapi.service.SwitchClientBase;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;

/** Verimor Switch client. Services cover every operation; the method below is a shortcut. */
public final class SwitchClient extends SwitchClientBase {
    public SwitchClient(SwitchClientOptions options) {
        super(core(options));
    }

    private static ProductCore core(SwitchClientOptions options) {
        Objects.requireNonNull(options, "options");
        return new ProductCore(new Transport(options, RawOperations.DEFAULT_BASE_URI), options.credentials(), null);
    }

    /** Starts a call from {@code extension} to {@code destination}. */
    public String originate(String extension, String destination) throws IOException, InterruptedException {
        return originate(extension, destination, null);
    }

    public String originate(String extension, String destination, @Nullable String callerId)
            throws IOException, InterruptedException {
        return calls().originate(new OriginateCallPostRequest()
                .extension(extension)
                .destination(destination)
                .callerId(callerId));
    }
}
