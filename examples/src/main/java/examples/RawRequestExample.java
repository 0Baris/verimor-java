package examples;

import com.bariscemant.verimor.RawRequest;
import com.bariscemant.verimor.RawResponse;
import com.bariscemant.verimor.switchapi.SwitchClient;

public final class RawRequestExample {
    public static void run(SwitchClient calls) throws Exception {
        RawRequest request = new RawRequest();
        request.query().put("page", "1");
        RawResponse response = calls.raw().send("getCdrs", request);
        System.out.println(response.body());
    }
}
