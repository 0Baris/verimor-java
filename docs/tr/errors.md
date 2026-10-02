# Hatalar

| Durum | İstisna |
| --- | --- |
| HTTP 2xx dışı | `VerimorApiException` (`statusCode()`, `bodyKind()`, `body()`) |
| 2xx ama gövde boş, JSON değil, beklenen şekilde değil veya zorunlu alan eksik | `UnexpectedResponseException` |
| Ağ hatası | `IOException` (sarılmaz) |
| Zaman aşımı | `HttpTimeoutException` (sarılmaz) |
| Kesinti | `InterruptedException` |
| Boş kimlik bilgisi, eksik yol değeri, bilinmeyen raw operasyon | `IllegalArgumentException` |

`ErrorBodyKind`: `JSON`, `TEXT`, `EMPTY`, `BINARY`. JSON hata gövdesindeki `message`, `detail`, `error` veya `msg` alanı mesaja eklenir. Mesajlar kimlik bilgisi veya istek adresi içermez. Hiçbir hata durumunda istek tekrarlanmaz.
