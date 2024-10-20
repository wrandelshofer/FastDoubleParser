/*
 * @(#)JsonDoubleParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.JsonDoubleTestDataFactory.createLongRunningTestData;
import static ch.randelshofer.fastdoubleparser.JsonDoubleTestDataFactory.createRegularTestData;
import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toCharArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JsonDoubleParser}
 */
public final class JsonDoubleParserTest {
    public static final int EXPECTED_MAX_INPUT_LENGTH = Integer.MAX_VALUE - 4;
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
    @Disabled("long running test")
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int_longRunningTest() {
        ToDoubleFunction<NumberTestData> lambda = u -> JsonDoubleParser.parseDouble((u.input()), u.charOffset(), u.charLength());
        return createLongRunningTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)));
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
    @Disabled("long running test")
    public Stream<DynamicNode> dynamicTests_parseDouble_ByteArray_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> JsonDoubleParser.parseDouble(toByteArray(u.input()), u.charOffset(), u.charLength());
        return createLongRunningTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)));
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
    @Disabled("long running test")
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> JsonDoubleParser.parseDouble(toCharArray(u.input()), u.charOffset(), u.charLength());
        return createLongRunningTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)));
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
