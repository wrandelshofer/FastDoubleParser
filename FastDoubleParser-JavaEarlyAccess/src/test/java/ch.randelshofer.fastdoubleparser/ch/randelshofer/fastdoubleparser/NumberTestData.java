/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;


import java.util.function.Function;

public record NumberTestData(String title,
                             CharSequence input,
                             int charOffset, int charLength,
                             int byteOffset, int byteLength,
                             Number expectedValue,
                             String expectedErrorMessage
) {

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
}
