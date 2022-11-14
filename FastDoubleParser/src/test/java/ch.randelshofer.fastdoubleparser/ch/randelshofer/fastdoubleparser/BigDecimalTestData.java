/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.util.function.Supplier;

public record BigDecimalTestData(String title,
                                 CharSequence input,
                                 int charOffset, int charLength,
                                 int byteOffset, int byteLength,
                                 Supplier<BigDecimal> expectedValue
) {
    public BigDecimalTestData(CharSequence input, double expectedDoubleValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                () -> BigDecimal.valueOf(expectedDoubleValue)
        );
    }

    public BigDecimalTestData(CharSequence input, BigDecimal expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigDecimalTestData(CharSequence input, double expectedDoubleValue, int offset, int length) {
        this(input.toString(), input, offset, length, offset, length,
                () -> BigDecimal.valueOf(expectedDoubleValue)
        );
    }

    public BigDecimalTestData(String title, CharSequence input, double expectedDoubleValue) {
        this(title.contains(input) ? title : title + " " + input, input, 0, input.length(), 0, input.length(),
                () -> BigDecimal.valueOf(expectedDoubleValue)
        );
    }

    public BigDecimalTestData(String title, CharSequence input, BigDecimal expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigDecimalTestData(String title, CharSequence input, boolean valid) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> valid ? new BigDecimal(input.toString()) : null
        );
    }

    public BigDecimalTestData(String title, CharSequence input, Supplier<BigDecimal> expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                expectedValue
        );
    }

    public BigDecimalTestData(CharSequence input) {
        this(input.toString(), input);
    }

    public BigDecimalTestData(String title, CharSequence input) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> null
        );
    }
}
