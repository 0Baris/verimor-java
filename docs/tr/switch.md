# Switch

```java
SwitchClient calls = new SwitchClient(new SwitchClientOptions("..."));

calls.originate("101", "905000000000");
calls.calls().hangup("call-id");
List<GetQueues200ResponseInner> queues = calls.queues().listQueues();
GetCdrs200Response cdrs = calls.records().listCdrs(null, null, null, null, null, null, null, null, 1L, 50L);
```

İsteğe bağlı parametresi olan her yöntemin yalnız zorunlu parametreleri alan bir eşi vardır (ör. `listCdrs()`). Servisler: `announcements()`, `blacklist()`, `callerIds()`, `calls()`, `contacts()`, `crm()`, `fax()`, `ivrCampaigns()`, `queues()`, `records()`, `users()`. Faks belgesi indirme `byte[]` döner. Tüm yöntemler [operasyon tablosunda](operations.md).
