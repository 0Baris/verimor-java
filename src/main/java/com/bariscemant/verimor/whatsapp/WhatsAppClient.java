package com.bariscemant.verimor.whatsapp;

import com.bariscemant.verimor.internal.ProductCore;
import com.bariscemant.verimor.internal.Transport;
import com.bariscemant.verimor.whatsapp.generated.model.MessageResponse;
import com.bariscemant.verimor.whatsapp.generated.model.TemplateMessageRequest;
import com.bariscemant.verimor.whatsapp.service.RawOperations;
import com.bariscemant.verimor.whatsapp.service.WhatsAppClientBase;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

/** Verimor WhatsApp client. Services cover every operation; the methods below are shortcuts. */
public final class WhatsAppClient extends WhatsAppClientBase {
    public WhatsAppClient(WhatsAppClientOptions options) {
        super(core(options));
    }

    private static ProductCore core(WhatsAppClientOptions options) {
        Objects.requireNonNull(options, "options");
        return new ProductCore(new Transport(options, RawOperations.DEFAULT_BASE_URI), options.credentials(), null);
    }

    public MessageResponse sendOtp(String to, String templateName, @Nullable String language, @Nullable List<String> parameters)
            throws IOException, InterruptedException {
        return messages().sendOtp(template(to, templateName, language, parameters));
    }

    public MessageResponse sendUtility(String to, String templateName, @Nullable String language, @Nullable List<String> parameters)
            throws IOException, InterruptedException {
        return messages().sendUtility(template(to, templateName, language, parameters));
    }

    private static TemplateMessageRequest template(
            String to, String templateName, @Nullable String language, @Nullable List<String> parameters) {
        return new TemplateMessageRequest().to(to).templateName(templateName).language(language).parameters(parameters);
    }
}
