/*
 * @(#)TestData.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.function.Supplier;

public record BigIntegerTestData(String title,
                                 CharSequence input,
                                 int charOffset, int charLength,
                                 int byteOffset, int byteLength,
                                 Supplier<BigInteger> expectedValue
) {
    public BigIntegerTestData(CharSequence input, long expectedLongValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                () -> BigInteger.valueOf(expectedLongValue)
        );
    }

    public BigIntegerTestData(CharSequence input, BigInteger expectedValue) {
        this(input.toString(), input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigIntegerTestData(CharSequence input, long expectedLongValue, int offset, int length) {
        this(input.toString(), input, offset, length, offset, length,
                () -> BigInteger.valueOf(expectedLongValue)
        );
    }

    public BigIntegerTestData(String title, CharSequence input, long expectedLongValue) {
        this(title.contains(input) ? title : title + " " + input, input, 0, input.length(), 0, input.length(),
                () -> BigInteger.valueOf(expectedLongValue)
        );
    }

    public BigIntegerTestData(String title, CharSequence input, BigInteger expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> expectedValue
        );
    }

    public BigIntegerTestData(String title, CharSequence input, Supplier<BigInteger> expectedValue) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                expectedValue
        );
    }

    public BigIntegerTestData(CharSequence input) {
        this(input.toString(), input);
    }

    public BigIntegerTestData(String title, CharSequence input) {
        this(title.contains(input) || title.length() + (long) input.length() > 100
                        ? title
                        : title + " " + input,
                input, 0, input.length(), 0, input.length(),
                () -> null
        );
    }
}
