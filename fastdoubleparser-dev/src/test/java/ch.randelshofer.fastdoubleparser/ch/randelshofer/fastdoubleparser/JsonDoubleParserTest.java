/*
 * @(#)JsonDoubleParserTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JsonDoubleParser}
 */
public class JsonDoubleParserTest extends AbstractJsonFloatValueParserTest {

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int_longRunningTest() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_ByteArray() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.byteLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8)))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_ByteArray_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8), u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_ByteArray_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8), u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.byteLength() <= AbstractFloatValueParser.MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().toCharArray()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().toCharArray(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().toCharArray(), u.charOffset(), u.charLength()))));
    }


    private void test(NumberTestData d, ToDoubleFunction<NumberTestData> f) {
        if (d.expectedErrorMessage() != null) {
            try {
                double actual = f.applyAsDouble(d);
                fail("should throw an exception but returned " + actual);
            } catch (IllegalArgumentException e) {
                if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                    e.printStackTrace();
                    assertEquals(d.expectedErrorMessage(), e.getMessage());
                }
                assertEquals(d.expectedThrowableClass(), e.getClass());
            }
        } else {
            double actual = f.applyAsDouble(d);
            assertEquals(d.expectedValue().doubleValue(), actual);
        }
    }

}
