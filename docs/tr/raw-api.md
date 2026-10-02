# Raw erişim

Her istemcinin `raw()` yöntemi, ürünün tüm operasyonlarına operasyon kimliğiyle erişim verir. Kimlik bilgileri ve varsayılan SMS başlığı yine istemci tarafından eklenir.

```java
RawRequest request = new RawRequest();
request.query().put("page", "1");
RawResponse response = calls.raw().send("getCdrs", request);
System.out.println(response.body());

for (RawOperation operation : calls.raw().operations()) {
    System.out.println(operation.operationId() + " " + operation.method() + " " + operation.pathTemplate());
}
```

Bilinmeyen operasyon kimliği istek gönderilmeden `IllegalArgumentException` ile reddedilir.
