/*
 * @(#)FloatValueFromVector.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;

import java.nio.ByteOrder;

import static jdk.incubator.vector.VectorOperators.ADD;
import static jdk.incubator.vector.VectorOperators.UNSIGNED_GT;

/**
 * This is an experimental parser for long values from a byte array.
 */
public class LongFromByteArray {
    private static final IntVector POWERS_OF_10 = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{1000_0000, 100_0000, 10_0000, 10000, 1000, 100, 10, 1}, 0);

    /**
     * Parses a long that is up to 16 characters long, and has no surrounding
     * whitespace.
     *
     * @param str    the string
     * @param offset the offset of the long in str
     * @param length the length of the long in str
     * @return the parsed value
     */
    public long parseLongVectorized(byte[] str, int offset, int length) {
        if (length == 0 || length > 16) {
            throw new NumberFormatException();
        }
        boolean isNegative = str[offset] == '-';
        if (isNegative || str[offset] == '+') {
            offset++;
            length--;
            if (length == 0) {
                throw new NumberFormatException();
            }
        }

        VectorMask<Byte> rangeMask = ByteVector.SPECIES_128.indexInRange(offset, offset + length);
        ByteVector byteVector = ByteVector.fromArray(
                ByteVector.SPECIES_128, str, offset,
                rangeMask);

        // Most character will be digits, we subtract '0' once.
        ByteVector digits = byteVector.sub((byte) '0', rangeMask);

        // With an unsigned gt we only need to check for > 9 to find non-digit characters
        VectorMask<Byte> digitsMask = digits.compare(UNSIGNED_GT, 9);
        if (digitsMask.anyTrue()) {
            throw new NumberFormatException("illegal digits");
        }

        digits = digits.unslice(ByteVector.SPECIES_128.length() - length);


        long low = digits
                .castShape(IntVector.SPECIES_256, 1)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);

        long high = digits
                .castShape(IntVector.SPECIES_256, 0)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);

        long value = high * 1_0000_0000L + low;
        return isNegative ? -value : value;
    }

    /**
     * Parses a long that has no surrounding whitespace.
     *
     * @param str    the string
     * @param offset the offset of the long in str
     * @param length the length of the long in str
     * @return the parsed value
     */
    public long parseLong(byte[] str, int offset, int length) {
        if (length == 0) {
            throw new NumberFormatException();
        }

        boolean isNegative = str[offset] == '-';
        final long limit;
        long multmin;
        if (isNegative || str[offset] == '+') {
            offset++;
            length--;
            if (length == 0) {
                throw new NumberFormatException();
            }
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
            int digit = digit(str[offset]);
            if (digit < 0) {
                throw new NumberFormatException();
            }
            value = value * 10;
            value -= digit;
        }
        if (offset < end) {
            for (; offset < end; offset++) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                int digit = digit(str[offset]);
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

    private int digit(byte b) {
        int digit = b - '0';
        return (digit & 0xff) < 10 ? digit : -1;
    }

    public long parseLongDumb(byte[] str, int offset, int length) {

        VectorMask<Byte> rangeMask = ByteVector.SPECIES_128.indexInRange(offset, offset + length);
        ByteVector byteVector = (ByteVector) ByteVector.fromByteArray(
                ByteVector.SPECIES_128, str, offset, ByteOrder.BIG_ENDIAN, rangeMask);

        // Most character will be digits, we subtract '0' once.
        ByteVector digits = byteVector.sub((byte) '0'
                //        , rangeMask
        );

        // With an unsigned gt we only need to check for > 9 to find non-digit characters
        /*
        VectorMask<Byte> digitsMask = digits.compare(UNSIGNED_GT, 9);
        if (digitsMask.anyTrue()) {
            throw new NumberFormatException("illegal digits");
        }*/

        long low = digits
                .castShape(IntVector.SPECIES_256, 1)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);

        long high = digits
                .castShape(IntVector.SPECIES_256, 0)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);

        long value = high * 1_0000_0000L + low;
        return value;
    }


}
