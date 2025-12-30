/*
 * @(#)NumberTestData.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.util.Locale;
import java.util.function.Function;

public record NumberTestData(String title,
                             CharSequence input,
                             int charOffset, int charLength,
                             int byteOffset, int byteLength,
                             int radix, Number expectedValue,
                             String expectedErrorMessage,
                             Class<? extends Throwable> expectedThrowableClass,
                             java.util.Locale locale, NumberFormatSymbols symbols, boolean ignoreCase) {
    public NumberTestData(String title,
                          CharSequence input,
                          int charOffset, int charLength,
                          int byteOffset, int byteLength,
                          int radix, Number expectedValue,
                          String expectedErrorMessage,
                          Class<? extends Throwable> expectedThrowableClass,
                          java.util.Locale locale, NumberFormatSymbols symbols) {
        this(title,
                input,
                charOffset, charLength,
                byteOffset, byteLength,
                radix, expectedValue,
                expectedErrorMessage,
                expectedThrowableClass,
                locale, symbols, false);
    }

    public NumberTestData(CharSequence input, Number expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                10, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(Locale locale, CharSequence input, Number expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                10, expectedValue, null,
                null, locale, null, false);
    }

    public NumberTestData(String title, Locale locale, CharSequence input, Number expectedValue) {
        this(title, input, 0, input.length(), 0, input.length(),
                10, expectedValue, null,
                null, locale, null, false);
    }

    public NumberTestData(String title, Locale locale, NumberFormatSymbols symbols, CharSequence input, Number expectedValue) {
        this(title, input, 0, input.length(), 0, input.length(),
                10, expectedValue, null,
                null, locale, symbols, false);
    }

    public NumberTestData(String title, Locale locale, NumberFormatSymbols symbols, boolean ignoreCase, CharSequence input, Number expectedValue) {
        this(title, input, 0, input.length(), 0, input.length(),
                10, expectedValue, null,
                null, locale, symbols, ignoreCase);
    }

    public NumberTestData(CharSequence input, int radix, Number expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                radix, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, int radix, Number expectedValue) {
        this(title, input, 0, input.length(), 0, input.length(),
                radix, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, Number expectedValue, int offset, int length) {
        this(input.toString(), input, offset, length, offset, length,
                10, expectedValue, null, null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, int radix, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(title, input, 0, input.length(), 0, input.length(),
                radix, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, int radix, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                radix, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, Number expectedValue, int offset, int length, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(input.toString(), input, offset, length, offset, length,
                10, expectedValue, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }


    public NumberTestData(String title, CharSequence input, int offset, int length, int bOffset, int bLength, String expectedErrorMessage,
                          Class<? extends Throwable> expectedThrowableClass) {
        this(title, input, offset, length, bOffset, bLength,
                10, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, int offset, int length, int bOffset, int bLength, Number expectedValue) {
        this(title, input, offset, length, bOffset, bLength,
                10, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, Number expectedValue) {
        this(title,
                input, 0, input.length(), 0, input.length(),
                10, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, Function<String, Number> constructor) {
        this(title,
                input, 0, input.length(), 0, input.length(),
                constructor.apply(input.toString())
        );
    }

    public NumberTestData(CharSequence input, Function<String, Number> constructor) {
        this(input.toString(),
                input, 0, input.length(), 0, input.length(),
                constructor.apply(input.toString())
        );
    }

    public NumberTestData(CharSequence input) {
        this(input.toString(), input, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class);
    }

    public NumberTestData(String title, CharSequence input, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(title,
                input, 0, input.length(), 0, input.length(),
                10, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(input.toString(),
                input, 0, input.length(), 0, input.length(),
                10, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData withSymbols(NumberFormatSymbols s) {
        return new NumberTestData(title, input, charOffset, charLength, byteOffset, byteLength, radix, expectedValue, expectedErrorMessage, expectedThrowableClass, locale, s, ignoreCase);
    }
}
