/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;


import java.util.Objects;
import java.util.function.Function;

public final class NumberTestData {
    private final String title;
    private final CharSequence input;
    private final int charOffset;
    private final int charLength;
    private final int byteOffset;
    private final int byteLength;
    private final Number expectedValue;
    private final String expectedErrorMessage;

    public NumberTestData(String title,
                          CharSequence input,
                          int charOffset, int charLength,
                          int byteOffset, int byteLength,
                          Number expectedValue,
                          String expectedErrorMessage
    ) {
        this.title = title;
        this.input = input;
        this.charOffset = charOffset;
        this.charLength = charLength;
        this.byteOffset = byteOffset;
        this.byteLength = byteLength;
        this.expectedValue = expectedValue;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    public NumberTestData(CharSequence input, Number expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                expectedValue, null
        );
    }

    public NumberTestData(CharSequence input, Number expectedValue, int offset, int length) {
        this(input.toString(), input, offset, length, offset, length,
                expectedValue, null
        );
    }

    public NumberTestData(CharSequence input, Number expectedValue, int offset, int length, String expectedErrorMessage) {
        this(input.toString(), input, offset, length, offset, length,
                expectedValue, expectedErrorMessage
        );
    }

    public NumberTestData(String title, CharSequence input, int offset, int length, int bOffset, int bLength, String expectedErrorMessage) {
        this(title, input, offset, length, bOffset, bLength,
                null, expectedErrorMessage
        );
    }

    public NumberTestData(String title, CharSequence input, int offset, int length, int bOffset, int bLength, Number expectedValue) {
        this(title, input, offset, length, bOffset, bLength,
                expectedValue, null
        );
    }

    public NumberTestData(String title, CharSequence input, Number expectedValue) {
        this(title,
                input, 0, input.length(), 0, input.length(),
                expectedValue, null
        );
    }

    public NumberTestData(String title, CharSequence input, Function<String, Number> constructor) {
        this(title,
                input, 0, input.length(), 0, input.length(),
                constructor.apply(input.toString())
        );
    }

    public NumberTestData(CharSequence input) {
        this(input.toString(), input, AbstractNumberParser.SYNTAX_ERROR);
    }

    public NumberTestData(String title, CharSequence input, String expectedErrorMessage) {
        this(title,
                input, 0, input.length(), 0, input.length(),
                null, expectedErrorMessage
        );
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

    public Number expectedValue() {
        return expectedValue;
    }

    public String expectedErrorMessage() {
        return expectedErrorMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (NumberTestData) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.input, that.input) &&
                this.charOffset == that.charOffset &&
                this.charLength == that.charLength &&
                this.byteOffset == that.byteOffset &&
                this.byteLength == that.byteLength &&
                Objects.equals(this.expectedValue, that.expectedValue) &&
                Objects.equals(this.expectedErrorMessage, that.expectedErrorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, input, charOffset, charLength, byteOffset, byteLength, expectedValue, expectedErrorMessage);
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
                "expectedValue=" + expectedValue + ", " +
                "expectedErrorMessage=" + expectedErrorMessage + ']';
    }

}
