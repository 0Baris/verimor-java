package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bariscemant.verimor.support.Contract;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DocumentationContractTest {
    private static String read(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    @ParameterizedTest
    @ValueSource(strings = {"tr", "en"})
    void theOperationTablesListEveryServiceMethod(String language) throws Exception {
        String table = read("docs/" + language + "/operations.md");
        for (Contract.Operation operation : Contract.load()) {
            assertTrue(table.contains("`" + operation.proxy + "`"), operation.proxy);
            assertTrue(table.contains("`" + operation.operationId + "`"), operation.operationId);
        }
    }

    @Test
    void bothReadmesStateTheUnofficialAndOfflineStatus() throws Exception {
        String turkish = read("README.md");
        String english = read("README.en.md");
        assertTrue(turkish.contains("resmî değildir"));
        assertTrue(turkish.contains("canlı Verimor servisine karşı henüz doğrulanmamıştır"));
        assertTrue(english.contains("unofficial"));
        assertTrue(english.contains("has not been validated against the live Verimor services"));
    }

    @Test
    void noDocumentCallsTheProductPbx() throws Exception {
        try (Stream<Path> docs = Files.walk(Path.of("docs"))) {
            List<Path> files = docs.filter(p -> p.toString().endsWith(".md")).collect(Collectors.toList());
            files.add(Path.of("README.md"));
            files.add(Path.of("README.en.md"));
            for (Path file : files) {
                assertFalse(Files.readString(file).toUpperCase(Locale.ROOT).contains("PBX"), file.toString());
            }
        }
    }

    @Test
    void everyGuideExistsInBothLanguagesAndSixExamplesShip() throws Exception {
        try (Stream<Path> tr = Files.list(Path.of("docs/tr")); Stream<Path> en = Files.list(Path.of("docs/en"));
                Stream<Path> examples = Files.list(Path.of("examples/src/main/java/examples"))) {
            assertEquals(
                    tr.map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList()),
                    en.map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList()));
            assertEquals(6, examples.count());
        }
    }
}
