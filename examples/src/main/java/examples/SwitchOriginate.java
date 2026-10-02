package examples;

import com.bariscemant.verimor.switchapi.SwitchClient;
import com.bariscemant.verimor.switchapi.SwitchClientOptions;

public final class SwitchOriginate {
    public static void main(String[] args) throws Exception {
        SwitchClient calls = new SwitchClient(new SwitchClientOptions(System.getenv("VERIMOR_SWITCH_API_KEY")));
        System.out.println(calls.originate("101", "905000000000"));
    }
}
