# Raw access

Every client's `raw()` method reaches all of the product's operations by operation id. Credentials and the default SMS sender are still added by the client.

```java
RawRequest request = new RawRequest();
request.query().put("page", "1");
RawResponse response = calls.raw().send("getCdrs", request);
System.out.println(response.body());

for (RawOperation operation : calls.raw().operations()) {
    System.out.println(operation.operationId() + " " + operation.method() + " " + operation.pathTemplate());
}
```

An unknown operation id is rejected with `IllegalArgumentException` before any request is sent.
