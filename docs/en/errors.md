# Errors

| Situation | Exception |
| --- | --- |
| Non-2xx HTTP status | `VerimorApiException` (`statusCode()`, `bodyKind()`, `body()`) |
| 2xx with an empty, non-JSON or wrongly shaped body, or a missing required field | `UnexpectedResponseException` |
| Network failure | `IOException` (not wrapped) |
| Timeout | `HttpTimeoutException` (not wrapped) |
| Interruption | `InterruptedException` |
| Blank credentials, missing path value, unknown raw operation | `IllegalArgumentException` |

`ErrorBodyKind` is one of `JSON`, `TEXT`, `EMPTY`, `BINARY`. A `message`, `detail`, `error` or `msg` field in a JSON error body is added to the message. Messages never contain credentials or the request address. No error is ever retried.
