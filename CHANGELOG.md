# Değişiklik günlüğü / Changelog

Bu proje [Semantic Versioning](https://semver.org/) kullanır. / This project follows Semantic Versioning.

## 0.3.0

- 72 operasyonun her biri için çalıştırılabilir örnek: `examples/src/main/java/examples/operations/<ürün>/<Operasyon>Example.java`, her biri kendi `main` metoduyla. Örnekler sözleşmeden üretilir; CI hepsini Verimor'a bağlanmadan yerel bir kayıt sunucusuna karşı çalıştırıp her birinin belgelenen isteği gönderdiğini doğrular (`scripts/run_examples.py java`).
- Yapay zekâ asistanları için tek dosyalık başvuru: `llms.md`. Kütüphane kodu ve API'ler 0.2.1 ile aynıdır.
- Runnable example for each of the 72 operations: `examples/src/main/java/examples/operations/<product>/<Operation>Example.java`, each with its own `main`. The examples are generated from the contract; CI runs every one against a local recording server, without contacting Verimor, and checks each sends the documented request (`scripts/run_examples.py java`).
- A single-file reference for AI assistants: `llms.md`. Library code and APIs are unchanged from 0.2.1.

## 0.2.1

- Sunucu adresi açıklamaları netleşti: varsayılan Verimor'un adresidir; kendi sunucunuz veya proxy için değiştirilebilir, IP, port ve alt yol korunur (testle doğrulandı).
- Server URL docs clarified: Verimor's address is the default and can be changed to your own server or proxy; an IP, a port and a path prefix are kept (now tested).

## 0.2.0

- Verimor'un yeni operasyonları: SMS `campaigns().sendOtp(...)` (`POST /v2/otp`); WhatsApp `messages().sendBulk(...)`, `listMessages(...)` ve `getMessage(...)`. Kapsam 72 operasyon: SMS 14, Switch 52, WhatsApp 6.
- Jackson 2.22 ile `-Werror` derlemesini kıran kullanımdan kaldırılmış ayar değiştirildi; Jackson 2.15–2.22 desteklenir.
- Verimor's new operations: SMS `campaigns().sendOtp(...)` (`POST /v2/otp`); WhatsApp `messages().sendBulk(...)`, `listMessages(...)` and `getMessage(...)`. Coverage is 72 operations: 14 SMS, 52 Switch, 6 WhatsApp.
- Replaced a deprecated Jackson setter that broke `-Werror` builds with Jackson 2.22; Jackson 2.15–2.22 is supported.

## 0.1.0 - Yayın adayı / Release candidate

- Java 11+ ve Kotlin için SMS, Switch ve WhatsApp istemcileri.
- Alan servisleri ve `raw()` erişimiyle 68 operasyonun tamamı.
- HTTP hata normalizasyonu, 30 saniyelik zaman aşımı ve otomatik tekrar içermeyen güvenli varsayılanlar.
- Offline sözleşme, localhost, paket içeriği ve temiz Java/Kotlin consumer testleri.

Canlı Verimor servisi doğrulaması ve Maven Central yayını henüz yapılmamıştır.

- SMS, Switch and WhatsApp clients for Java 11+ and Kotlin.
- All 68 operations through domain services and `raw()` access.
- HTTP error normalization, a 30-second timeout and no automatic retries.
- Offline contract, localhost, package-content and clean Java/Kotlin consumer tests.

Live Verimor validation and Maven Central publication have not been performed yet.
