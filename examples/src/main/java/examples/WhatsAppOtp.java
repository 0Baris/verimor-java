package examples;

import com.bariscemant.verimor.whatsapp.WhatsAppClient;
import com.bariscemant.verimor.whatsapp.WhatsAppClientOptions;
import com.bariscemant.verimor.whatsapp.generated.model.MessageResponse;
import java.util.List;

public final class WhatsAppOtp {
    public static void main(String[] args) throws Exception {
        WhatsAppClient whatsApp = new WhatsAppClient(new WhatsAppClientOptions(System.getenv("VERIMOR_WHATSAPP_API_KEY")));
        MessageResponse accepted = whatsApp.sendOtp("905000000000", "otp_template", "tr", List.of("123456"));
        System.out.println(accepted.getId() + " " + accepted.getStatus());
    }
}
