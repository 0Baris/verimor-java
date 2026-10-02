# Configuration

Each product has its own options class; credentials are required in the constructor and the rest is set with fluent methods.

| Method | Meaning |
| --- | --- |
| `baseUri(URI)` | Server address. Defaults to Verimor's address for the product; set it to use another server such as a proxy. An IP, a port and a path prefix are kept. |
| `timeout(Duration)` | Per-request time limit; 30 seconds by default. Zero or negative values are rejected. |
| `httpClient(HttpClient)` | A client you own (proxy, TLS, executor). The SDK never closes it or changes its settings. |

- `new SmsClientOptions(username, password).defaultSender("VERIMOR")`
- `new SwitchClientOptions(key)`
- `new WhatsAppClientOptions(apiKey)` (the `x-api-key` header)

Blank credentials are rejected with `IllegalArgumentException`; the message never contains the secret. Each client sends only its own credentials.

The SDK never retries. If you need retries, add them in your application and only for operations that are safe to repeat (a repeated SMS send may deliver twice). In Kotlin, call from `withContext(Dispatchers.IO)`; every method blocks.
