# WhatsApp

```java
WhatsAppClient whatsApp = new WhatsAppClient(new WhatsAppClientOptions("..."));

MessageResponse otp = whatsApp.sendOtp("905000000000", "otp_template", "en", List.of("123456"));
MessageResponse utility = whatsApp.sendUtility("905000000000", "order_update", "en", List.of("42"));
System.out.println(otp.getId() + " " + otp.getStatus());
```

Message endpoints return `202 Accepted`. If the body is not the expected `MessageResponse` or misses a required field, `UnexpectedResponseException` is raised. `health().health()` sends no credentials.

`messages()` also offers `sendBulk(BulkMessageRequest)` for one template to up to 10,000 recipients, `listMessages(...)` to filter sent messages and `getMessage(messageRef)` for one message's current status.
