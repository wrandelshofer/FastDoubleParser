/*
 * @(#)JavaDoubleParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toCharArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JavaDoubleParser}
 */
public class JavaDoubleParserTest extends AbstractJavaDoubleParserTest {

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence() {
        return createRegularDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_CharSequence_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> JavaDoubleParser.parseDouble(u.input(), u.charOffset(), u.charLength());
        return createLongRunningDoubleTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_byteArray() {
        return createRegularDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= AbstractFloatValueParser.MAX_INPUT_LENGTH
                        && t.byteOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(toByteArray(u.input())))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_byteArray_int_int() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(toByteArray(u.input()), u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_byteArray_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> JavaDoubleParser.parseDouble(toByteArray(u.input()), u.charOffset(), u.charLength());
        return createLongRunningDoubleTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray() {
        return createRegularDoubleTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(toCharArray(u.input())))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray_int_int() {
        return createRegularDoubleTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaDoubleParser.parseDouble(toCharArray(u.input()), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseDouble_charArray_int_int_longRunningTests() {
        ToDoubleFunction<NumberTestData> lambda = u -> JavaDoubleParser.parseDouble(toCharArray(u.input()), u.charOffset(), u.charLength());
        return createLongRunningDoubleTestData()
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
