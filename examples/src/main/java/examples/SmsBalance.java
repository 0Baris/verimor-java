package examples;

import com.bariscemant.verimor.sms.SmsClient;

public final class SmsBalance {
    public static void run(SmsClient sms) throws Exception {
        System.out.println(sms.balance());
    }
}
