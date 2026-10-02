# SMS

```java
SmsClient sms = new SmsClient(new SmsClientOptions("...", "...").defaultSender("VERIMOR"));

sms.send("905000000000", "Merhaba");            // varsayılan başlık
sms.send("905000000000", "Merhaba", "DIGER");   // çağrı başına başlık
sms.balance();
sms.statusById(12345);
sms.statusByCustomId("siparis-42");
```

Ayrıntılı gönderim için üretilmiş istek modelini kullanın. `username` ve `password` alanlarına boş dize verin; istemci kendi kimlik bilgilerini doldurur:

```java
SendSmsJsonRequest request = new SendSmsJsonRequest()
        .username("").password("")
        .messages(List.of(new SendSmsJsonRequestMessagesInner().dest("905000000000").msg("Merhaba")));
sms.campaigns().send(request);
```

Servisler: `balances()`, `blacklist()`, `campaigns()`, `iys()`, `reports()`, `senderIds()`. Tüm yöntemler [operasyon tablosunda](operations.md).
