# Katkı

[English](CONTRIBUTING.en.md)

`src/main/java/com/bariscemant/verimor/*/generated/`, `*/service/`, `contracts/operations.json` ve `docs/*/operations.md` üretilmiş dosyalardır; elle değiştirmeyin. Bu dosyalardaki hatalar kaynak tarafta düzeltilip yeniden dışa aktarılır. Diğer tüm dosyalar bu depoya aittir.

Değişiklik göndermeden önce:

```bash
mvn verify
sh scripts/test-consumer.sh
```

Testler gerçek kimlik bilgisi veya canlı servis kullanmamalıdır.
