/*
 * @(#)JavaDoubleParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests class {@link JavaDoubleParser}
 * <p>
 * The goal of this test is to achieve 100% code coverage (in the class, method, line and branch metric)
 * in the following source files:
 * <ul>
 *     <li>{@link AbstractFloatValueParser}</li>
 *     <li>{@link AbstractJavaFloatingPointBitsFromByteArray}</li>
 *     <li>{@link AbstractJavaFloatingPointBitsFromCharArray}</li>
 *     <li>{@link AbstractJavaFloatingPointBitsFromCharSequence}</li>
 *     <li>{@link JavaDoubleBitsFromByteArray}</li>
 *     <li>{@link JavaDoubleBitsFromCharArray}</li>
 *     <li>{@link JavaDoubleBitsFromCharSequence}</li>
 *     <li>{@link JavaDoubleParser}</li>
 * </ul>
 */
public final class JavaDoubleParserTest {


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaDoubleParserDataSource#parseDouble_CharSequence")
    public void parseDouble_CharSequence(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            double actual = JavaDoubleParser.parseDouble(d.input());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaDoubleParser.parseDouble(d.input()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaDoubleParserDataSource#parseDouble_CharSequence_int_int")
    public void parseDouble_CharSequence_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            double actual = JavaDoubleParser.parseDouble(d.input(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaDoubleParser.parseDouble(d.input(), d.charOffset(), d.charLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaDoubleParserDataSource#parseDouble_byteArray")
    public void parseDouble_byteArray(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            double actual = JavaDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8));
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaDoubleParserDataSource#parseDouble_byteArray_int_int")
    public void parseDouble_byteArray_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            double actual = JavaDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaDoubleParserDataSource#parseDouble_charArray")
    public void parseDouble_charArray(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            double actual = JavaDoubleParser.parseDouble(d.input().toString().toCharArray());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaDoubleParser.parseDouble(d.input().toString().toCharArray()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JavaDoubleParserDataSource#parseDouble_charArray_int_int")
    public void parseDouble_charArray_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            double actual = JavaDoubleParser.parseDouble(d.input().toString().toCharArray(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JavaDoubleParser.parseDouble(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        }
    }


}
