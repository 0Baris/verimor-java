package examples;

import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.SmsClientOptions;

public final class SendSms {
    public static void main(String[] args) throws Exception {
        SmsClient sms = new SmsClient(new SmsClientOptions(
                System.getenv("VERIMOR_SMS_USERNAME"), System.getenv("VERIMOR_SMS_PASSWORD"))
                .defaultSender("VERIMOR"));
        System.out.println(sms.send("905000000000", "Merhaba"));
    }
}
