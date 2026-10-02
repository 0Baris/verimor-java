# Kurulum

Java 11 veya üstü gerekir. Bağımlılıklar: `jackson-databind`, `jackson-datatype-jsr310` ve `jsr305`. HTTP için yalnız JDK'nın `java.net.http.HttpClient`'ı kullanılır.

Paket Maven Central'da yayımlanana kadar GitHub Release'teki `verimor-0.2.0.jar` dosyasını yerel Maven deposuna kurabilirsiniz:

```bash
mvn install:install-file -Dfile=verimor-0.2.0.jar -DgroupId=io.github.0baris -DartifactId=verimor -Dversion=0.2.0 -Dpackaging=jar
```

veya depoyu klonlayıp `mvn install` çalıştırabilirsiniz. Gradle: `implementation("io.github.0baris:verimor:0.2.0")` (yerel `mavenLocal()` deposuyla).
