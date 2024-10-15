/*
 * @(#)NumberTestData.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public final class NumberTestData {
    private final String title;
    private final CharSequence input;
    private final int charOffset;
    private final int charLength;
    private final int byteOffset;
    private final int byteLength;
    private final int radix;
    private final Number expectedValue;
    private final String expectedErrorMessage;
    private final Class<? extends Throwable> expectedThrowableClass;
    private final Locale locale;
    private final NumberFormatSymbols symbols;
    private final boolean ignoreCase;

    public NumberTestData(String title,
                          CharSequence input,
                          int charOffset, int charLength,
                          int byteOffset, int byteLength,
                          int radix, Number expectedValue,
                          String expectedErrorMessage,
                          Class<? extends Throwable> expectedThrowableClass,
                          Locale locale, NumberFormatSymbols symbols, boolean ignoreCase) {
        this.title = title;
        this.input = input;
        this.charOffset = charOffset;
        this.charLength = charLength;
        this.byteOffset = byteOffset;
        this.byteLength = byteLength;
        this.radix = radix;
        this.expectedValue = expectedValue;
        this.expectedErrorMessage = expectedErrorMessage;
        this.expectedThrowableClass = expectedThrowableClass;
        this.locale = locale;
        this.symbols = symbols;
        this.ignoreCase = ignoreCase;
    }

    public NumberTestData(String title,
                          CharSequence input,
                          int charOffset, int charLength,
                          int byteOffset, int byteLength,
                          int radix, Number expectedValue,
                          String expectedErrorMessage,
                          Class<? extends Throwable> expectedThrowableClass,
                          Locale locale, NumberFormatSymbols symbols) {
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

    public String title() {
        return title;
    }

    public CharSequence input() {
        return input;
    }

    public int charOffset() {
        return charOffset;
    }

    public int charLength() {
        return charLength;
    }

    public int byteOffset() {
        return byteOffset;
    }

    public int byteLength() {
        return byteLength;
    }

    public int radix() {
        return radix;
    }

    public Number expectedValue() {
        return expectedValue;
    }

    public String expectedErrorMessage() {
        return expectedErrorMessage;
    }

    public Class<? extends Throwable> expectedThrowableClass() {
        return expectedThrowableClass;
    }

    public Locale locale() {
        return locale;
    }

    public NumberFormatSymbols symbols() {
        return symbols;
    }

    public boolean ignoreCase() {
        return ignoreCase;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        NumberTestData that = (NumberTestData) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.input, that.input) &&
                this.charOffset == that.charOffset &&
                this.charLength == that.charLength &&
                this.byteOffset == that.byteOffset &&
                this.byteLength == that.byteLength &&
                this.radix == that.radix &&
                Objects.equals(this.expectedValue, that.expectedValue) &&
                Objects.equals(this.expectedErrorMessage, that.expectedErrorMessage) &&
                Objects.equals(this.expectedThrowableClass, that.expectedThrowableClass) &&
                Objects.equals(this.locale, that.locale) &&
                Objects.equals(this.symbols, that.symbols) &&
                this.ignoreCase == that.ignoreCase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, input, charOffset, charLength, byteOffset, byteLength, radix, expectedValue, expectedErrorMessage, expectedThrowableClass, locale, symbols, ignoreCase);
    }

    @Override
    public String toString() {
        return "NumberTestData[" +
                "title=" + title + ", " +
                "input=" + input + ", " +
                "charOffset=" + charOffset + ", " +
                "charLength=" + charLength + ", " +
                "byteOffset=" + byteOffset + ", " +
                "byteLength=" + byteLength + ", " +
                "radix=" + radix + ", " +
                "expectedValue=" + expectedValue + ", " +
                "expectedErrorMessage=" + expectedErrorMessage + ", " +
                "expectedThrowableClass=" + expectedThrowableClass + ", " +
                "locale=" + locale + ", " +
                "symbols=" + symbols + ", " +
                "ignoreCase=" + ignoreCase + ']';
    }

}
