/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.function.Supplier;

public record BigIntegerTestData(String title,
                                 String input,
                                 int charOffset, int charLength,
                                 int byteOffset, int byteLength,
                                 Supplier<BigInteger> expectedValue
) {
    public BigIntegerTestData(String input, long expectedLongValue) {
        this(input, input, 0, input.length(), 0, input.length(),
                () -> BigInteger.valueOf(expectedLongValue)
        );
    }

    public BigIntegerTestData(String input, BigInteger expectedValue) {
        this(input, input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigIntegerTestData(String input, long expectedLongValue, int offset, int length) {
        this(input, input, offset, length, offset, length,
                () -> BigInteger.valueOf(expectedLongValue)
        );
    }

    public BigIntegerTestData(String title, String input, long expectedLongValue) {
        this(title.contains(input) ? title : title + " " + input, input, 0, input.length(), 0, input.length(),
                () -> BigInteger.valueOf(expectedLongValue)
        );
    }

    public BigIntegerTestData(String title, String input, BigInteger expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigIntegerTestData(String title, String input, Supplier<BigInteger> expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                expectedValue
        );
    }

    public BigIntegerTestData(String input) {
        this(input, input);
    }

    public BigIntegerTestData(String title, String input) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> null
        );
    }
}
