/*
 * @(#)JavaFloatParserTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.util.Objects;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toCharArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JavaDoubleParser}
 */
public class JavaFloatParserTest extends AbstractJavaFloatValueParserTest {
    private boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));

    @TestFactory
    public Stream<DynamicNode> dynamicTests_JavaFloatParser_parseFloat_CharSequence() {
        return createAllFloatTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= AbstractFloatValueParser.MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_JavaFloatParser_parseFloat_CharSequence_Int_Int() {
        return createAllFloatTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_JavaFloatParser_parseFloat_ByteArray() {
        return createAllFloatTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.byteLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t,
                                u -> JavaFloatParser.parseFloat(toByteArray(u.input())
                                ))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_JavaFloatParser_parseFloat_ByteArray_int_int() {
        return createAllFloatTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(toByteArray(u.input()),
                                u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_JavaFloatParser_parseFloat_CharArray() {
        return createAllFloatTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(toCharArray(u.input())))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_JavaFloatParser_parseFloat_CharArray_int_int() {
        return createAllFloatTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(toCharArray(u.input()),
                                u.charOffset(), u.charLength()))));
    }


    protected void test(NumberTestData d, ToFloatFunction<NumberTestData> f) {
        if (d.expectedErrorMessage() != null) {
            try {
                float actual = f.applyAsFloat(d);
                fail("should throw an exception but returned " + actual);
            } catch (IllegalArgumentException e) {
                if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                    e.printStackTrace();
                    assertEquals(d.expectedErrorMessage(), e.getMessage());
                }
                assertEquals(d.expectedThrowableClass(), e.getClass());
            }
        } else {
            float actual = f.applyAsFloat(d);
            assertEquals(d.expectedValue().floatValue(), actual);
        }
    }


    @FunctionalInterface
    public interface ToFloatFunction<T> {

        float applyAsFloat(T value);
    }
}
