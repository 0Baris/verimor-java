# Yapılandırma

Her ürünün kendi seçenek sınıfı vardır; kimlik bilgileri kurucuda zorunludur, diğer ayarlar akıcı (fluent) yöntemlerle verilir.

| Yöntem | Açıklama |
| --- | --- |
| `baseUri(URI)` | Ürünün varsayılan adresini değiştirir (ör. yerel test sunucusu). |
| `timeout(Duration)` | İstek başına süre sınırı; varsayılan 30 saniye. Sıfır veya negatif değer reddedilir. |
| `httpClient(HttpClient)` | Sizin yönettiğiniz istemci (proxy, TLS, executor). SDK onu kapatmaz ve ayarlarını değiştirmez. |

- `new SmsClientOptions(username, password).defaultSender("VERIMOR")`
- `new SwitchClientOptions(key)`
- `new WhatsAppClientOptions(apiKey)` (`x-api-key` başlığı)

Boş kimlik bilgisi `IllegalArgumentException` ile reddedilir; mesaj gizli değeri içermez. Her istemci yalnız kendi kimlik bilgilerini gönderir.

SDK otomatik tekrar denemez. Tekrar gerekiyorsa uygulamanızda ve yalnız tekrarlanması güvenli işlemler için yapın (SMS gönderimi tekrarlanırsa mesaj iki kez gidebilir). Kotlin'de çağrıları `withContext(Dispatchers.IO)` içinde yapın; tüm yöntemler bloklayıcıdır.
