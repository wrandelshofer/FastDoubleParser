/*
 * @(#)ConfigurableDoubleParserTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests class {@link ConfigurableDoubleParser}
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
 *     <li>{@link ConfigurableDoubleParser}</li>
 * </ul>
 */
public final class ConfigurableDoubleParserTest {


    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserDataSource#parseDouble_CharSequence")
    public void parseDouble_CharSequence(NumberTestData d) {
        NumberFormatSymbols symbols = d.symbols() == null ? NumberFormatSymbols.fromDefault() : d.symbols();
        if (d.expectedThrowableClass() == null) {
            double actual = new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserDataSource#parseDouble_CharSequence_int_int")
    public void parseDouble_CharSequence_int_int(NumberTestData d) {
        NumberFormatSymbols symbols = d.symbols() == null ? NumberFormatSymbols.fromDefault() : d.symbols();
        if (d.expectedThrowableClass() == null) {
            double actual = new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input(), d.charOffset(), d.charLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserDataSource#parseDouble_byteArray")
    public void parseDouble_byteArray(NumberTestData d) {
        NumberFormatSymbols symbols = d.symbols() == null ? NumberFormatSymbols.fromDefault() : d.symbols();
        if (d.expectedThrowableClass() == null) {
            double actual = new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8));
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input().toString().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserDataSource#parseDouble_byteArray_int_int")
    public void parseDouble_byteArray_int_int(NumberTestData d) {
        NumberFormatSymbols symbols = d.symbols() == null ? NumberFormatSymbols.fromDefault() : d.symbols();
        byte[] inputBytes = d.input().toString().getBytes(StandardCharsets.UTF_8);
        if (d.expectedThrowableClass() == null) {
            double actual = new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(inputBytes, d.byteOffset(), d.byteLength());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(inputBytes, d.byteOffset(), d.byteLength()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserDataSource#parseDouble_charArray")
    public void parseDouble_charArray(NumberTestData d) {
        NumberFormatSymbols symbols = d.symbols() == null ? NumberFormatSymbols.fromDefault() : d.symbols();
        if (d.expectedThrowableClass() == null) {
            double actual = new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input().toString().toCharArray());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input().toString().toCharArray()));
        }
    }

    @ParameterizedTest
    @MethodSource("ch.randelshofer.fastdoubleparser.ConfigurableDoubleParserDataSource#parseDouble_charArray_int_int")
    public void parseDouble_charArray_int_int(NumberTestData d) {
        NumberFormatSymbols symbols = d.symbols() == null ? NumberFormatSymbols.fromDefault() : d.symbols();
        if (d.expectedThrowableClass() == null) {
            double actual = new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input().toString().toCharArray(), d.charOffset(), d.charLength());
            assertEquals(d.expectedValue().doubleValue(), actual);
        } else {
            MoreAssertions.assertThrows(d.expectedErrorMessage(), d.expectedThrowableClass(), () -> new ConfigurableDoubleParser(symbols, d.ignoreCase()).parseDouble(d.input().toString().toCharArray(), d.charOffset(), d.charLength()));
        }
    }

}
