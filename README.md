# Verimor Java SDK

[English](README.en.md)

Verimor SMS, Switch ve WhatsApp API'leri için Java 11+ ve Kotlin uygulamalarında kullanılabilen bağımsız topluluk SDK'sı. 72 operasyonun tamamı alan servisleri ve `raw()` erişimiyle kullanılabilir.

> Bu proje topluluk tarafından sürdürülür ve resmî değildir. Verimor adına destek veya uyumluluk garantisi vermez.
>
> Bu sürüm offline sözleşme ve localhost testleriyle doğrulanmıştır; canlı Verimor servisine karşı henüz doğrulanmamıştır.

## Kurulum

Maven:

```xml
<dependency>
  <groupId>io.github.0baris</groupId>
  <artifactId>verimor</artifactId>
  <version>0.3.0</version>
</dependency>
```

Paket henüz Maven Central'da değil; GitHub Release'teki jar'ı yerel depoya kurabilirsiniz (bkz. [Kurulum](docs/tr/installation.md)). Kimlik bilgilerini yalnız sunucu tarafında, ortam değişkeni veya secret manager içinde tutun.

## Hızlı başlangıç

### SMS

```java
SmsClient sms = new SmsClient(new SmsClientOptions(
        System.getenv("VERIMOR_SMS_USERNAME"), System.getenv("VERIMOR_SMS_PASSWORD"))
        .defaultSender("VERIMOR"));

String campaignId = sms.send("905000000000", "Merhaba");
String balance = sms.balance();
List<GetSmsStatus200ResponseInner> statuses = sms.statusById(12345);
// veya: sms.statusByCustomId("siparis-42");
```

`defaultSender` her gönderimde `source_addr` olarak kullanılır; `send(hedef, mesaj, "DIGER")` ile tek çağrıda değiştirebilirsiniz. İkisi de yoksa alan gönderilmez.

### Switch

```java
SwitchClient calls = new SwitchClient(new SwitchClientOptions(System.getenv("VERIMOR_SWITCH_API_KEY")));
String callId = calls.originate("101", "905000000000");
```

Switch operasyonları alan servislerindedir: `calls()`, `contacts()`, `queues()`, `records()`, `users()` ve diğerleri.

### WhatsApp

```java
WhatsAppClient whatsApp = new WhatsAppClient(new WhatsAppClientOptions(System.getenv("VERIMOR_WHATSAPP_API_KEY")));
MessageResponse accepted = whatsApp.sendOtp("905000000000", "otp_template", "tr", List.of("123456"));
```

### Kotlin

```kotlin
val sms = SmsClient(SmsClientOptions(username, password).defaultSender("VERIMOR"))
val id: String = sms.send("905000000000", "Merhaba")
```

Paketler JSR-305 açıklamaları taşır; `-Xjsr305=strict` ile Kotlin gerçek null tiplerini görür. Kotlin, Java metotlarında isimli argüman desteklemez.

## Davranış

- Java 11+ gerektirir ve yalnız JDK'nın `java.net.http.HttpClient`'ını kullanır; bağımlılıklar Jackson ve `jsr305`'tir.
- Varsayılan zaman aşımı 30 saniyedir; SDK hiçbir isteği otomatik tekrarlamaz.
- Kendi `HttpClient`'ınızı `httpClient(...)` ile verebilirsiniz; SDK onu kapatmaz ve ayarlarını değiştirmez.
- 2xx dışındaki yanıtlar `VerimorApiException`, beklenmeyen 2xx gövdeleri `UnexpectedResponseException` olur. Ağ hataları ve zaman aşımı `IOException` (ör. `HttpTimeoutException`) olarak kalır.

## Örnekler

72 operasyonun her biri için çalıştırılabilir bir örnek [`examples/src/main/java/examples/operations/`](examples/src/main/java/examples/operations/) altındadır; her sınıfın kendi `main` metodu vardır. Her dosya kimlik bilgilerini ortam değişkenlerinden, sunucu adresini `VERIMOR_BASE_URL` değişkeninden okur; değişken yoksa Verimor'un sunucusuna gider. `scripts/run_examples.py` hepsini Verimor'a hiç bağlanmadan yerel bir kayıt sunucusuna karşı çalıştırır.

Yapay zekâ asistanları için tek dosyalık başvuru: [`llms.md`](llms.md).

## Belgeler

- [Kurulum](docs/tr/installation.md) · [Yapılandırma](docs/tr/configuration.md) · [SMS](docs/tr/sms.md) · [Switch](docs/tr/switch.md) · [WhatsApp](docs/tr/whatsapp.md)
- [Hatalar](docs/tr/errors.md) · [Raw erişim](docs/tr/raw-api.md) · [Test ve güvenlik](docs/tr/testing-and-safety.md) · [Tüm operasyonlar](docs/tr/operations.md)

## Lisans

MIT. Bkz. [LICENSE](LICENSE).
