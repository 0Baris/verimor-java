# Switch

```java
SwitchClient calls = new SwitchClient(new SwitchClientOptions("..."));

calls.originate("101", "905000000000");
calls.calls().hangup("call-id");
List<GetQueues200ResponseInner> queues = calls.queues().listQueues();
GetCdrs200Response cdrs = calls.records().listCdrs(null, null, null, null, null, null, null, null, 1L, 50L);
```

Every method with optional parameters also has an overload that takes only the required ones (for example `listCdrs()`). Services: `announcements()`, `blacklist()`, `callerIds()`, `calls()`, `contacts()`, `crm()`, `fax()`, `ivrCampaigns()`, `queues()`, `records()`, `users()`. The fax document download returns `byte[]`. Every method is in the [operation table](operations.md).
