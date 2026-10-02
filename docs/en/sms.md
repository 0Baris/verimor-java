# SMS

```java
SmsClient sms = new SmsClient(new SmsClientOptions("...", "...").defaultSender("VERIMOR"));

sms.send("905000000000", "Hello");            // default sender
sms.send("905000000000", "Hello", "OTHER");   // per-call sender
sms.balance();
sms.statusById(12345);
sms.statusByCustomId("order-42");
```

For detailed sends use the generated request model. Pass empty strings for `username` and `password`; the client fills in its own credentials:

```java
SendSmsJsonRequest request = new SendSmsJsonRequest()
        .username("").password("")
        .messages(List.of(new SendSmsJsonRequestMessagesInner().dest("905000000000").msg("Hello")));
sms.campaigns().send(request);
```

Services: `balances()`, `blacklist()`, `campaigns()`, `iys()`, `reports()`, `senderIds()`. Every method is in the [operation table](operations.md).
