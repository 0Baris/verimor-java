import com.bariscemant.verimor.sms.SmsClient
import com.bariscemant.verimor.sms.SmsClientOptions
import com.bariscemant.verimor.switchapi.SwitchClient
import com.bariscemant.verimor.switchapi.SwitchClientOptions
import com.bariscemant.verimor.whatsapp.WhatsAppClient
import com.bariscemant.verimor.whatsapp.WhatsAppClientOptions
import com.sun.net.httpserver.HttpServer
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.CopyOnWriteArrayList

// Kotlin use of the installed artifact; -Xjsr305=strict makes the nullability annotations binding.
fun main() {
    val seen = CopyOnWriteArrayList<String>()
    val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0)
    server.createContext("/") { exchange ->
        val body = exchange.requestBody.readAllBytes().decodeToString()
        seen += "${exchange.requestMethod} ${exchange.requestURI.rawPath}?${exchange.requestURI.rawQuery} " +
            "${exchange.requestHeaders.getFirst("x-api-key")} $body"
        val message = exchange.requestURI.path.startsWith("/v1/messages/")
        val reply = (if (message) """{"id":"3f2504e0-4f89-41d3-9a0c-0305e82c3301","status":"queued"}""" else "42")
            .encodeToByteArray()
        exchange.responseHeaders.add("Content-Type", if (message) "application/json" else "text/plain")
        exchange.sendResponseHeaders(if (message) 202 else 200, reply.size.toLong())
        exchange.responseBody.write(reply)
        exchange.close()
    }
    server.start()
    val base = URI.create("http://127.0.0.1:${server.address.port}/")
    try {
        val sms = SmsClient(SmsClientOptions("consumer-user", "consumer-pass").defaultSender("KOTLIN").baseUri(base))
        val calls = SwitchClient(SwitchClientOptions("consumer-key").baseUri(base))
        val whatsApp = WhatsAppClient(WhatsAppClientOptions("consumer-api-key").baseUri(base))

        val balance: String = sms.balance()
        check(balance == "42") { "SMS balance" }
        // Kotlin cannot use named arguments with Java methods; arguments are positional.
        check(calls.originate("1001", "905551112233") == "42") { "Switch originate" }
        val status: String = whatsApp.sendOtp("905551112233", "otp", null, null).status
        check(status == "queued") { "WhatsApp OTP" }
        check(sms.send("905551112233", "merhaba") == "42") { "SMS send" }

        val log = seen.joinToString("\n")
        check("\"source_addr\":\"KOTLIN\"" in log) { "default sender" }
        check("POST /originate?key=consumer-key" in log) { "Switch key" }
        println("Installed package consumer passed for SMS, Switch and WhatsApp (Kotlin).")
    } finally {
        server.stop(0)
    }
}
