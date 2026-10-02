import com.bariscemant.verimor.sms.SmsClient;
import com.bariscemant.verimor.sms.SmsClientOptions;
import com.bariscemant.verimor.switchapi.SwitchClient;
import com.bariscemant.verimor.switchapi.SwitchClientOptions;
import com.bariscemant.verimor.whatsapp.WhatsAppClient;
import com.bariscemant.verimor.whatsapp.WhatsAppClientOptions;
import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Uses only the installed io.github.0baris:verimor artifact and calls each product on loopback. */
public final class Consumer {
    public static void main(String[] args) throws Exception {
        List<String> seen = new CopyOnWriteArrayList<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            URI uri = exchange.getRequestURI();
            seen.add(exchange.getRequestMethod() + " " + uri.getRawPath() + "?" + uri.getRawQuery() + " "
                    + exchange.getRequestHeaders().getFirst("x-api-key") + " " + body);
            boolean message = uri.getPath().startsWith("/v1/messages/");
            byte[] reply = (message ? "{\"id\":\"3f2504e0-4f89-41d3-9a0c-0305e82c3301\",\"status\":\"queued\"}" : "42")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", message ? "application/json" : "text/plain");
            exchange.sendResponseHeaders(message ? 202 : 200, reply.length);
            exchange.getResponseBody().write(reply);
            exchange.close();
        });
        server.start();
        URI base = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
        try {
            SmsClient sms = new SmsClient(new SmsClientOptions("consumer-user", "consumer-pass").baseUri(base));
            SwitchClient calls = new SwitchClient(new SwitchClientOptions("consumer-key").baseUri(base));
            WhatsAppClient whatsApp = new WhatsAppClient(new WhatsAppClientOptions("consumer-api-key").baseUri(base));

            require("42".equals(sms.balance()), "SMS balance");
            require("42".equals(calls.originate("1001", "905551112233")), "Switch originate");
            require("queued".equals(whatsApp.sendOtp("905551112233", "otp", null, null).getStatus()), "WhatsApp OTP");

            String log = String.join("\n", seen);
            require(log.contains("GET /v2/balance?password=consumer-pass&username=consumer-user"), "SMS credentials");
            require(log.contains("POST /originate?key=consumer-key"), "Switch key");
            require(log.contains("POST /v1/messages/otp?null consumer-api-key"), "WhatsApp key header");
            System.out.println("Installed package consumer passed for SMS, Switch and WhatsApp (Java).");
        } finally {
            server.stop(0);
        }
    }

    private static void require(boolean condition, String what) {
        if (!condition) {
            throw new IllegalStateException("Consumer check failed: " + what);
        }
    }
}
