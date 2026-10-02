package com.bariscemant.verimor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.bariscemant.verimor.internal.CallParts;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class CallPartsTest {
    @Test
    void skipsNullsAndFormatsScalarsLocaleIndependently() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            CallParts call = new CallParts()
                    .query("skip", null).query("n", 42L).query("t", true).query("f", false)
                    .query("s", "text").query("ids", List.of(1L, 2L, 3L)).query("d", 1.5d);
            assertFalse(call.query().containsKey("skip"));
            assertEquals("42", call.query().get("n"));
            assertEquals("true", call.query().get("t"));
            assertEquals("false", call.query().get("f"));
            assertEquals("text", call.query().get("s"));
            assertEquals("1,2,3", call.query().get("ids"));
            assertEquals("1.5", call.query().get("d"));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
