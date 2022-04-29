/*
 * @(#)FloatValueFromVector.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;

import java.nio.charset.StandardCharsets;

import static jdk.incubator.vector.VectorOperators.ADD;
import static jdk.incubator.vector.VectorOperators.EQ;
import static jdk.incubator.vector.VectorOperators.UNSIGNED_GT;

/**
 * This is an experimental mostly vectorized parser for float values from a
 * byte array.
 */
public class VectorizedFloatFromByteArray {
    private static final IntVector POWERS_OF_10 = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{1000_0000, 100_0000, 10_0000, 10000, 1000, 100, 10, 1}, 0);


    /**
     * Parses a {@code FloatValue} without leading or trailing whitespace.
     *
     * @param str    a string
     * @param offset start offset
     * @param length length
     * @return the parsed value
     */
    public float parseFloat(byte[] str, int offset, int length) {
        ByteVector byteVector = ByteVector.fromArray(
                ByteVector.SPECIES_128, str, offset,
                ByteVector.SPECIES_128.indexInRange(offset, offset + length));

        // Get indices of '-' signs of significand and of exponent number
        VectorMask<Byte> signMask = byteVector.compare(EQ, '-');
        int isNegative = signMask.laneIsSet(0) ? 1 : 0;// isNegative==1 means true!

        // Most character will be digits, we subtract '0' once.
        ByteVector digitsVector = byteVector.sub((byte) '0');

        // parse the exponent
        // ------------------

        // get index of 'e' or 'E' character
        VectorMask<Byte> exponentMask = byteVector.compare(EQ, 'e').or(byteVector.compare(EQ, 'E'));
        int exponentIndex = exponentMask.lastTrue();

        int significandEndIndex;
        long exponentNumber;
        if (exponentIndex >= 0) {
            // The exponent number starts after the optional sign of the
            // exponent and ends at length.
            int exponentSignIndex = signMask.lastTrue();
            int isExponentNegative = exponentSignIndex > exponentIndex ? 1 : 0;// isExponentNegative==1 means true!

            int exponentDigitCount = length - exponentIndex - 1 - isExponentNegative;
            //To prevent overflow, we only use up to 8 digits.
            exponentNumber = parseDigits(digitsVector.unslice(ByteVector.SPECIES_128.length() - length),
                    Math.min(8, exponentDigitCount));
            significandEndIndex = exponentIndex;
            if (isExponentNegative != 0) {
                exponentNumber = -exponentNumber;
            }
        } else {
            significandEndIndex = length;
            exponentNumber = 0L;
        }

        // parse the significand
        // ---------------------
        int pointIndex = byteVector.compare(EQ, '.').lastTrue();
        int significandDigitsCount = significandEndIndex - isNegative;
        int exponentOfSignifand;
        if (pointIndex >= 0) {
            //-> keep fraction in place; move integer part to the right by 1.
            int intPartDigitsCount = pointIndex - isNegative;
            if (intPartDigitsCount > 0) {
                ByteVector unslicedIntDigits = digitsVector.unslice(1);
                VectorMask<Byte> digitsMask = VectorMask.fromLong(ByteVector.SPECIES_128, 0xffff >>> 15 - pointIndex);
                digitsVector = digitsVector.blend(unslicedIntDigits, digitsMask);
            }
            significandDigitsCount -= 1;
            exponentOfSignifand = pointIndex - significandDigitsCount - isNegative;
        } else {
            exponentOfSignifand = 0;
        }
        long significand = parseDigits(digitsVector.unslice(16 - significandEndIndex), significandDigitsCount);


        int exponent = (int) (exponentOfSignifand + exponentNumber);

        float result = FastFloatMath.decFloatLiteralToFloat(isNegative != 0, significand, exponent, false, 0);
        return Float.isNaN(result) ? Float.parseFloat(new String(str, offset, length, StandardCharsets.ISO_8859_1)) : result;

    }

    /**
     * Parses up to 16 digits at once.
     *
     * @param digits         (ByteVector.SPECIES_128) with digit characters
     *                       adjusted to the right, and '0' subtracted from
     *                       all characters.
     * @param numberOfDigits number of digit characters
     * @return the parsed value
     */
    private long parseDigits(ByteVector digits, int numberOfDigits) {
        VectorMask<Byte> digitsMask = VectorMask.fromLong(ByteVector.SPECIES_128, 0xffff >>> numberOfDigits);
        digits = digits.blend(0, digitsMask);

        // With an unsigned gt we only need to check for > 9 to find non-digit characters
        if (digits.compare(UNSIGNED_GT, 9).anyTrue()) {
            throw new NumberFormatException("illegal digits");
        }

        long low = digits
                .castShape(IntVector.SPECIES_256, 1)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);

        long high = digits
                .castShape(IntVector.SPECIES_256, 0)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);
        return high * 1_0000_0000L + low;
    }
}
