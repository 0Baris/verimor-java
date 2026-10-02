# Installation

Java 11 or newer is required. Dependencies: `jackson-databind`, `jackson-datatype-jsr310` and `jsr305`. HTTP uses only the JDK's `java.net.http.HttpClient`.

Until the package is on Maven Central, install `verimor-0.2.1.jar` from the GitHub Release into your local Maven repository:

```bash
mvn install:install-file -Dfile=verimor-0.2.1.jar -DgroupId=io.github.0baris -DartifactId=verimor -Dversion=0.2.1 -Dpackaging=jar
```

or clone the repository and run `mvn install`. Gradle: `implementation("io.github.0baris:verimor:0.2.1")` (with the `mavenLocal()` repository).
