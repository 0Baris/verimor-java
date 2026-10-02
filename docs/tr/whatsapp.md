# WhatsApp

```java
WhatsAppClient whatsApp = new WhatsAppClient(new WhatsAppClientOptions("..."));

MessageResponse otp = whatsApp.sendOtp("905000000000", "otp_template", "tr", List.of("123456"));
MessageResponse utility = whatsApp.sendUtility("905000000000", "order_update", "tr", List.of("42"));
System.out.println(otp.getId() + " " + otp.getStatus());
```

Mesaj uçları `202 Accepted` döner. Gövde beklenen `MessageResponse` şeklinde değilse veya zorunlu alanlar eksikse `UnexpectedResponseException` atılır. `health().health()` kimlik bilgisi göndermez.

`messages()` ayrıca tek şablonu en fazla 10.000 alıcıya kuyruğa alan `sendBulk(BulkMessageRequest)`, gönderilen mesajları filtreleyen `listMessages(...)` ve tek mesajın güncel durumunu döndüren `getMessage(messageRef)` metotlarını sunar.
