# Verimor Java SDK

[Türkçe](README.md)

An independent community SDK for the Verimor SMS, Switch and WhatsApp APIs, for Java 11+ and Kotlin applications. All 72 operations are available through domain services and `raw()` access.

> This project is community-maintained and unofficial. It provides no support or compatibility guarantee on behalf of Verimor.
>
> This release is verified with offline contract and localhost tests; it has not been validated against the live Verimor services yet.

## Installation

Maven:

```xml
<dependency>
  <groupId>io.github.0baris</groupId>
  <artifactId>verimor</artifactId>
  <version>0.2.0</version>
</dependency>
```

The package is not on Maven Central yet; install the jar from the GitHub Release into your local repository (see [Installation](docs/en/installation.md)). Keep credentials on the server side only, in environment variables or a secret manager.

## Quick start

### SMS

```java
SmsClient sms = new SmsClient(new SmsClientOptions(
        System.getenv("VERIMOR_SMS_USERNAME"), System.getenv("VERIMOR_SMS_PASSWORD"))
        .defaultSender("VERIMOR"));

String campaignId = sms.send("905000000000", "Hello");
String balance = sms.balance();
List<GetSmsStatus200ResponseInner> statuses = sms.statusById(12345);
// or: sms.statusByCustomId("order-42");
```

`defaultSender` is sent as `source_addr` on every send; `send(destination, message, "OTHER")` overrides it for one call. When neither is set the field is omitted.

### Switch

```java
SwitchClient calls = new SwitchClient(new SwitchClientOptions(System.getenv("VERIMOR_SWITCH_API_KEY")));
String callId = calls.originate("101", "905000000000");
```

Switch operations live in domain services: `calls()`, `contacts()`, `queues()`, `records()`, `users()` and more.

### WhatsApp

```java
WhatsAppClient whatsApp = new WhatsAppClient(new WhatsAppClientOptions(System.getenv("VERIMOR_WHATSAPP_API_KEY")));
MessageResponse accepted = whatsApp.sendOtp("905000000000", "otp_template", "en", List.of("123456"));
```

### Kotlin

```kotlin
val sms = SmsClient(SmsClientOptions(username, password).defaultSender("VERIMOR"))
val id: String = sms.send("905000000000", "Hello")
```

The packages carry JSR-305 annotations; with `-Xjsr305=strict` Kotlin sees real nullability. Kotlin does not support named arguments for Java methods.

## Behavior

- Requires Java 11+ and uses only the JDK's `java.net.http.HttpClient`; dependencies are Jackson and `jsr305`.
- The default timeout is 30 seconds; the SDK never retries a request.
- Pass your own `HttpClient` with `httpClient(...)`; the SDK never closes it or changes its settings.
- Non-2xx responses raise `VerimorApiException`, unexpected 2xx bodies raise `UnexpectedResponseException`. Network failures and timeouts stay `IOException` (for example `HttpTimeoutException`).

## Documentation

- [Installation](docs/en/installation.md) · [Configuration](docs/en/configuration.md) · [SMS](docs/en/sms.md) · [Switch](docs/en/switch.md) · [WhatsApp](docs/en/whatsapp.md)
- [Errors](docs/en/errors.md) · [Raw access](docs/en/raw-api.md) · [Testing and safety](docs/en/testing-and-safety.md) · [All operations](docs/en/operations.md)

## License

MIT. See [LICENSE](LICENSE).
