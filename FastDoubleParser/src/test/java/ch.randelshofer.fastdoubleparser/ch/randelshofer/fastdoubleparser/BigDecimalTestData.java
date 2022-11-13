/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.util.function.Supplier;

public record BigDecimalTestData(String title,
                                 String input,
                                 int charOffset, int charLength,
                                 int byteOffset, int byteLength,
                                 Supplier<BigDecimal> expectedValue
) {
    public BigDecimalTestData(String input, double expectedDoubleValue) {
        this(input, input, 0, input.length(), 0, input.length(),
                () -> BigDecimal.valueOf(expectedDoubleValue)
        );
    }

    public BigDecimalTestData(String input, BigDecimal expectedValue) {
        this(input, input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigDecimalTestData(String input, double expectedDoubleValue, int offset, int length) {
        this(input, input, offset, length, offset, length,
                () -> BigDecimal.valueOf(expectedDoubleValue)
        );
    }

    public BigDecimalTestData(String title, String input, double expectedDoubleValue) {
        this(title.contains(input) ? title : title + " " + input, input, 0, input.length(), 0, input.length(),
                () -> BigDecimal.valueOf(expectedDoubleValue)
        );
    }

    public BigDecimalTestData(String title, String input, BigDecimal expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigDecimalTestData(String title, String input, Supplier<BigDecimal> expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                expectedValue
        );
    }

    public BigDecimalTestData(String input) {
        this(input, input);
    }

    public BigDecimalTestData(String title, String input) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> null
        );
    }
}
