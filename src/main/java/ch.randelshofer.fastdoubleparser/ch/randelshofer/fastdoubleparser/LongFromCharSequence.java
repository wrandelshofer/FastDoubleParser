/*
 * @(#)FloatValueFromVector.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * This is an experimental parser for long values from a {@link CharSequence}.
 */
public class LongFromCharSequence {

    /**
     * Parses a long that has no surrounding whitespace.
     *
     * @param str    the string
     * @param offset the offset of the long in str
     * @param length the length of the long in str
     * @return the parsed value
     */
    public long parseLong(CharSequence str, int offset, int length) {
        if (length == 0) {
            throw new NumberFormatException();
        }

        boolean isNegative = str.charAt(offset) == '-';
        final long limit;
        long multmin;
        if (isNegative || str.charAt(offset) == '+') {
            length--;
            if (length == 0) {
                throw new NumberFormatException();
            }
            offset++;
            limit = Long.MIN_VALUE;
            multmin = Long.MIN_VALUE / 10;
        } else {
            limit = -Long.MAX_VALUE;
            multmin = -Long.MAX_VALUE / 10;
        }

        long value = 0;
        int end = offset + length;

        for (int endNoOverflow = offset + Math.min(18, length); offset < endNoOverflow; offset++) {
            // We can not overflow in the first 18 digits
            // Accumulating negatively avoids surprises near MAX_VALUE
            int digit = digit(str.charAt(offset));
            if (digit < 0) {
                throw new NumberFormatException();
            }
            value = value * 10;
            value -= digit;
        }
        if (offset < end) {
            for (; offset < end; offset++) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                int digit = digit(str.charAt(offset));
                if (digit < 0 || value < multmin) {
                    throw new NumberFormatException();
                }
                value = value * 10;
                if (value < limit + digit) {
                    throw new NumberFormatException();
                }
                value -= digit;
            }
        }

        return isNegative ? value : -value;
    }

    private int digit(char b) {
        int digit = b - '0';
        return (digit & 0xffff) < 10 ? digit : -1;
    }
}
