/*
 * @(#)JsonDoubleParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;

/**
 * Tests class {@link JsonDoubleParser}
 * <p>
 * The goal of this test is to achieve 100% code coverage (in the class, method, line and branch metric)
 * in the following source files:
 * <ul>
 *     <li>{@link AbstractFloatValueParser}</li>
 *     <li>{@link AbstractJsonFloatingPointBitsFromByteArray}</li>
 *     <li>{@link AbstractJsonFloatingPointBitsFromCharArray}</li>
 *     <li>{@link AbstractJsonFloatingPointBitsFromCharSequence}</li>
 *     <li>{@link JsonDoubleBitsFromByteArray}</li>
 *     <li>{@link JsonDoubleBitsFromCharArray}</li>
 *     <li>{@link JsonDoubleBitsFromCharSequence}</li>
 *     <li>{@link JsonDoubleParser}</li>
 * </ul>
 */
public final class JsonDoubleParserTest {


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JsonDoubleParserDataSource#parseDouble_CharSequence")
    public void parseDouble_CharSequence(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            JsonDoubleParser.parseDouble(d.input());
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JsonDoubleParser.parseDouble(d.input()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JsonDoubleParserDataSource#parseDouble_CharSequence_int_int")
    public void parseDouble_CharSequence_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            JsonDoubleParser.parseDouble(d.input(), d.charOffset(), d.charLength());
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JsonDoubleParser.parseDouble(d.input(), d.charOffset(), d.charLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JsonDoubleParserDataSource#parseDouble_byteArray")
    public void parseDouble_byteArray(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            JsonDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8));
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JsonDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JsonDoubleParserDataSource#parseDouble_byteArray_int_int")
    public void parseDouble_byteArray_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            JsonDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength());
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JsonDoubleParser.parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8), d.byteOffset(), d.byteLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JsonDoubleParserDataSource#parseDouble_charArray")
    public void parseDouble_charArray(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            JsonDoubleParser.parseDouble(d.input().toString().toCharArray());
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JsonDoubleParser.parseDouble(d.input().toString().toCharArray()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.JsonDoubleParserDataSource#parseDouble_charArray_int_int")
    public void parseDouble_charArray_int_int(NumberTestData d) {
        if (d.expectedThrowableClass() == null) {
            JsonDoubleParser.parseDouble(d.input().toString().toCharArray(), d.charOffset(), d.charLength());
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> JsonDoubleParser.parseDouble(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        }
    }


}
