package examples;

import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.generated.model.GetSmsStatus200ResponseInner;

public final class SmsStatus {
    public static void run(SmsClient sms) throws Exception {
        for (GetSmsStatus200ResponseInner status : sms.statusByCustomId("order-42")) {
            System.out.println(status);
        }
    }
}
