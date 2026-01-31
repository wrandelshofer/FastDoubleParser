/*
 * @(#)NumberTestData.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.util.Locale;
import java.util.Objects;
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
        this(input.toString(), input, 0, input.length(), 0, getByteLength(input),
                10, expectedValue, null,
                null, locale, null, false);
    }

    public NumberTestData(String title, Locale locale, CharSequence input, Number expectedValue) {
        this(title, input, 0, input.length(), 0, getByteLength(input),
                10, expectedValue, null,
                null, locale, null, false);
    }

    public NumberTestData(String title, Locale locale, NumberFormatSymbols symbols, CharSequence input, Number expectedValue) {
        this(title, input, 0, input.length(), 0, getByteLength(input),
                10, expectedValue, null,
                null, locale, symbols, false);
    }

    public NumberTestData(String title, Locale locale, NumberFormatSymbols symbols, boolean ignoreCase, CharSequence input, Number expectedValue) {
        this(title, input, 0, input.length(), 0, getByteLength(input),
                10, expectedValue, null,
                null, locale, symbols, ignoreCase);
    }

    public NumberTestData(CharSequence input, int radix, Number expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, getByteLength(input),
                radix, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, int radix, Number expectedValue) {
        this(title, input, 0, input.length(), 0, getByteLength(input),
                radix, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, Number expectedValue, int offset, int length) {
        this(input.toString(), input, offset, length, getByteLength(input.subSequence(0, offset)), getByteLength(input.subSequence(offset, offset + length)),
                10, expectedValue, null, null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, int radix, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(title, input, 0, input.length(), 0, getByteLength(input),
                radix, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, int radix, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(input.toString(), input, 0, input.length(), 0, getByteLength(input),
                radix, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, Number expectedValue, int offset, int length, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(input.toString(), input, offset, length, getByteLength(input.subSequence(0, offset)), getByteLength(input.subSequence(offset, offset + length)),
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
                input, 0, input.length(), 0, getByteLength(input),
                10, expectedValue, null,
                null, Locale.ENGLISH, null, false);
    }

    public NumberTestData(String title, CharSequence input, Function<String, Number> constructor) {
        this(title,
                input, 0, input.length(), 0, getByteLength(input),
                constructor.apply(input.toString())
        );
    }

    public NumberTestData(CharSequence input, Function<String, Number> constructor) {
        this(input.toString(),
                input, 0, input.length(), 0, getByteLength(input),
                constructor.apply(input.toString())
        );
    }

    public NumberTestData(CharSequence input) {
        this(input.toString(), input, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class);
    }

    public NumberTestData(String title, CharSequence input) {
        this(title, input, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class);
    }

    public NumberTestData(String title, CharSequence input, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(title,
                input, 0, input.length(), 0, getByteLength(input),
                10, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    public NumberTestData(CharSequence input, String expectedErrorMessage, Class<? extends Throwable> expectedThrowableClass) {
        this(input.toString(),
                input, 0, input.length(), 0, getByteLength(input),
                10, null, expectedErrorMessage,
                expectedThrowableClass, Locale.ENGLISH, null, false);
    }

    private static int getByteLength(CharSequence input) {
        return input.codePoints().map(c ->
                c < 0x0080 ? 1 :
                        c < 0x0800 ? 2 :
                                c < 0xffff ? 3 : 4
        ).sum();
    }

    public NumberTestData withSymbols(NumberFormatSymbols s) {
        return new NumberTestData(title, input, charOffset, charLength, byteOffset, byteLength, radix, expectedValue, expectedErrorMessage, expectedThrowableClass, locale, s, ignoreCase);
    }

    public NumberTestData withException(String msg, Class<? extends Throwable> e) {
        return new NumberTestData(title, input, charOffset, charLength, byteOffset, byteLength, radix, expectedValue, msg, e, locale, symbols, ignoreCase);
    }

    @Override
    public String toString() {
        return "{" +
                '`' + title + '`' +
                (title.contentEquals(input) ? "" : ", input=" + input) +
                (charOffset == 0 ? "" : ", charOffset=" + charOffset) +
                (charLength == input.length() ? "" : ", charLength=" + charLength) +
                (byteOffset == 0 ? "" : ", byteOffset=" + byteOffset) +
                (byteLength == input.length() ? "" : ", byteLength=" + byteLength) +
                (radix == 10 ? "" : ", radix=" + radix) +
                (expectedValue == null ? "" : ", expectedValue=" + expectedValue) +
                (expectedErrorMessage == null ? "" : ", expectedErrorMessage='" + expectedErrorMessage + '\'') +
                (expectedThrowableClass == null ? "" : ", expectedThrowableClass=" + expectedThrowableClass) +
                (locale == Locale.ENGLISH ? "" : ", locale=" + locale) +
                (symbols == null ? "" : ", symbols=" + symbols) +
                (!ignoreCase ? "" : ", ignoreCase=" + ignoreCase) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumberTestData that = (NumberTestData) o;
        return radix == that.radix && charOffset == that.charOffset && charLength == that.charLength && byteOffset == that.byteOffset && byteLength == that.byteLength && ignoreCase == that.ignoreCase && Objects.equals(title, that.title) && Objects.equals(input, that.input) && Objects.equals(expectedValue, that.expectedValue) && Objects.equals(locale, that.locale) && Objects.equals(expectedErrorMessage, that.expectedErrorMessage) && Objects.equals(symbols, that.symbols) && Objects.equals(expectedThrowableClass, that.expectedThrowableClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, input, charOffset, charLength, byteOffset, byteLength, radix, expectedValue, expectedErrorMessage, expectedThrowableClass, locale, symbols, ignoreCase);
    }
}
