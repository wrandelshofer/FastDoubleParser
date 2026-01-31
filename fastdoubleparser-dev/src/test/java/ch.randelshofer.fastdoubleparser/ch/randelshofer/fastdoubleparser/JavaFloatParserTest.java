/*
 * @(#)JavaFloatParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests class {@link JavaFloatParser}
 * <p>
 * The goal of this test is to achieve 100% code coverage (in the class, method, line and branch metric)
 * in the following source files:
 * <ul>
 *     <li>{@link AbstractFloatValueParser}</li>
 *     <li>{@link AbstractJavaFloatingPointBitsFromByteArray}</li>
 *     <li>{@link AbstractJavaFloatingPointBitsFromCharArray}</li>
 *     <li>{@link AbstractJavaFloatingPointBitsFromCharSequence}</li>
 *     <li>{@link JavaFloatBitsFromByteArray}</li>
 *     <li>{@link JavaFloatBitsFromCharArray}</li>
 *     <li>{@link JavaFloatBitsFromCharSequence}</li>
 *     <li>{@link JavaFloatParser}</li>
 * </ul>
 */
public final class JavaFloatParserTest {


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaFloatParserDataSource#parseFloat_CharSequence")
    public void parseFloat_CharSequence(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            float actual = JavaFloatParser.parseFloat(d.input());
            assertEquals(d.expectedValue().floatValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaFloatParser.parseFloat(d.input()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaFloatParserDataSource#parseFloat_CharSequence_int_int")
    public void parseFloat_CharSequence_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            float actual = JavaFloatParser.parseFloat(d.input(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue().floatValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaFloatParser.parseFloat(d.input(), d.charOffset(), d.charLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaFloatParserDataSource#parseFloat_byteArray")
    public void parseFloat_byteArray(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            float actual = JavaFloatParser.parseFloat(d.input().toString().getBytes(StandardCharsets.UTF_8));
            assertEquals(d.expectedValue().floatValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaFloatParser.parseFloat(d.input().toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaFloatParserDataSource#parseFloat_byteArray_int_int")
    public void parseFloat_byteArray_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            float actual = JavaFloatParser.parseFloat(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength());
            assertEquals(d.expectedValue().floatValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaFloatParser.parseFloat(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaFloatParserDataSource#parseFloat_charArray")
    public void parseFloat_charArray(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            float actual = JavaFloatParser.parseFloat(d.input().toString().toCharArray());
            assertEquals(d.expectedValue().floatValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaFloatParser.parseFloat(d.input().toString().toCharArray()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaFloatParserDataSource#parseFloat_charArray_int_int")
    public void parseFloat_charArray_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            float actual = JavaFloatParser.parseFloat(d.input().toString().toCharArray(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue().floatValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaFloatParser.parseFloat(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        }
    }


}
