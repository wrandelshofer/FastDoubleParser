/*
 * @(#)DecimalFormatSymbolsTest.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecimalFormatSymbolsTest {
    @ParameterizedTest
    @CsvSource(value = {
            "ar\t10000.0\t10,000",
            "de-CH\t10000.0\t10'000"
    }, delimiter = '\t')
    public void shouldFormatExpectedLocalizedNumbersOn25AndLater(String language, String input, String expected) {
        if (checkVersion("25", "99.z")) {
            Locale locale = Locale.forLanguageTag(language);
            NumberFormat f = NumberFormat.getNumberInstance(locale);
            String actual = f.format(Double.valueOf(input));
            assertEquals(expected, actual, "language=" + language + ", input=" + input);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ar\t10000.0\t١٠٬٠٠٠",
            "de-CH\t10000.0\t10’000"
    }, delimiter = '\t')
    public void shouldFormatExpectedLocalizedNumbersOn11To24(String language, String input, String expected) {
        if (checkVersion("11", "24.z")) {
            Locale locale = Locale.forLanguageTag(language);
            NumberFormat f = NumberFormat.getNumberInstance(locale);
            String actual = f.format(Double.valueOf(input));
            assertEquals(expected, actual, "language=" + language + ", input=" + input);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ar\t10000.0\t10,000",
            "de-CH\t10000.0\t10'000"
    }, delimiter = '\t')
    public void shouldFormatExpectedLocalizedNumbersOn8(String language, String input, String expected) {
        if (checkVersion("1.8", "1.z")) {
            Locale locale = Locale.forLanguageTag(language);
            NumberFormat f = NumberFormat.getNumberInstance(locale);
            String actual = f.format(Double.valueOf(input));
            assertEquals(expected, actual, "language=" + language + ", input=" + input);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ar\t10,000\t10000.0",
            "de-CH\t10'000.0\t10000.0"
    }, delimiter = '\t')
    public void shouldParseExpectedLocalizedNumbersOn25AndLater(String language, String input, String expected) throws ParseException {
        if (checkVersion("25", "99.z")) {
            Locale locale = Locale.forLanguageTag(language);
            NumberFormat f = NumberFormat.getNumberInstance(locale);
            Number actual = f.parse(input);
            assertEquals(Double.parseDouble(expected), actual.doubleValue(), "language=" + language + ", input=" + input);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ar\t١٠٬٠٠٠\t10000.0",
            "de-CH\t10’000.0\t10000.0"
    }, delimiter = '\t')
    public void shouldParseExpectedLocalizedNumbersOn11To21(String language, String input, String expected) throws ParseException {
        if (checkVersion("11", "24.z")) {
            Locale locale = Locale.forLanguageTag(language);
            NumberFormat f = NumberFormat.getNumberInstance(locale);
            Number actual = f.parse(input);
            assertEquals(Double.parseDouble(expected), actual.doubleValue(), "language=" + language + ", input=" + input);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ar\t10,000\t10000.0",
            "de-CH\t10'000.0\t10000.0"
    }, delimiter = '\t')
    public void shouldParseExpectedLocalizedNumbersOn8(String language, String input, String expected) throws ParseException {
        if (checkVersion("1.8", "1.z")) {
            Locale locale = Locale.forLanguageTag(language);
            NumberFormat f = NumberFormat.getNumberInstance(locale);
            Number actual = f.parse(input);
            assertEquals(Double.parseDouble(expected), actual.doubleValue(), "language=" + language + ", input=" + input);
        }
    }

    private static boolean checkVersion(String from, String to) {
        String version = System.getProperty("java.version");
        return from.compareTo(version) <= 0 && version.compareTo(to) <= 0;
    }
}
