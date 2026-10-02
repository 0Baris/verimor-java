# Contributing

[Türkçe](CONTRIBUTING.md)

`src/main/java/com/bariscemant/verimor/*/generated/`, `*/service/`, `contracts/operations.json` and `docs/*/operations.md` are generated; do not edit them by hand. Defects in them are fixed at the source and re-exported. Every other file belongs to this repository.

Before sending a change:

```bash
mvn verify
sh scripts/test-consumer.sh
```

Tests must not use real credentials or the live service.
