/*
 * @(#)JavaDoubleParserTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JavaDoubleParser}
 */
public class JavaDoubleParserTest extends AbstractJavaFloatValueParserTest {
    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharSequence() {
        return createAllDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharSequenceIntInt() {
        return createAllDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleByteArray() {
        return createAllDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= AbstractNumberParser.MAX_INPUT_LENGTH
                        && t.byteOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8)))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleByteArrayIntInt() {
        return createAllDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8), u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharArray() {
        return createAllDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= AbstractNumberParser.MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input().toString().toCharArray()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharArrayIntInt() {
        return createAllDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input().toString().toCharArray(), u.charOffset(), u.charLength()))));
    }

    @Test
    public void emptyCharSequenceThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> {
            // to make explicit we are calling JavaDoubleParser.parseDouble(CharSequence)
            final CharSequence cs = "";
            JavaDoubleParser.parseDouble(cs);
        });
    }

    @Test
    public void emptyByteArrayThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> {
            JavaDoubleParser.parseDouble(new byte[0]);
        });
    }

    @Test
    public void emptyCharArrayThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> {
            JavaDoubleParser.parseDouble(new char[0]);
        });
    }

    private void test(NumberTestData d, ToDoubleFunction<NumberTestData> f) {
        if (d.expectedErrorMessage() != null) {
            try {
                f.applyAsDouble(d);
            } catch (Exception e) {
                if (!Objects.equals(e.getMessage(), d.expectedErrorMessage())) {
                    e.printStackTrace();
                }
                assertEquals(e.getMessage(), d.expectedErrorMessage());
            }
        } else {
            double actual = f.applyAsDouble(d);
            assertEquals(d.expectedValue().doubleValue(), actual);
        }
    }


}
