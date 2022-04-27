/*
 * @(#)PowersOf5Test.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static ch.randelshofer.fastdoubleparser.FastDoubleMath.MANTISSA_128;
import static ch.randelshofer.fastdoubleparser.FastDoubleMath.MANTISSA_64;
import static java.lang.Long.toUnsignedString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PowersOf10Test {
    /**
     * Tests if the values in {@link FastDoubleMath#MANTISSA_64} and
     * {@link FastDoubleMath#MANTISSA_128} are correct in the range
     * [0,{@value FastDoubleMath#DOUBLE_MAX_EXPONENT_POWER_OF_TEN}].
     */
    @Test
    public void testPositivePowersOf10() {
        // Note: We compute powers of 5 here.
        // Since {@literal 10 == 5<<1}, we obtain powers of 10 after we have
        // shifted the value to obtain the 128 most significant bits.
        BigInteger five = new BigInteger(new byte[]{5});
        BigInteger value = BigInteger.ONE;


        for (int p = 0; p <= FastDoubleMath.DOUBLE_MAX_EXPONENT_POWER_OF_TEN; p++) {
            long expectedHigh = MANTISSA_64[p - FastDoubleMath.DOUBLE_MIN_EXPONENT_POWER_OF_TEN];
            long expectedLow = MANTISSA_128[p - FastDoubleMath.DOUBLE_MIN_EXPONENT_POWER_OF_TEN];

            byte[] expectedBytes = new byte[17];
            FastDoubleSimd.readLongFromByteArrayBigEndian.set(expectedBytes, 1, expectedHigh);
            FastDoubleSimd.readLongFromByteArrayBigEndian.set(expectedBytes, 1 + 8, expectedLow);
            BigInteger expectedShifted = new BigInteger(expectedBytes);

            int bitLength = value.bitLength();
            BigInteger actualShifted = bitLength <= 128 ? value.shiftLeft(128 - bitLength) : value.shiftRight(bitLength - 128);
            byte[] actualBytes = actualShifted.toByteArray();
            long actualHigh = (long) FastDoubleSimd.readLongFromByteArrayBigEndian.get(actualBytes, 1);
            long actualLow = (long) FastDoubleSimd.readLongFromByteArrayBigEndian.get(actualBytes, 1 + 8);

            assertEquals(expectedShifted, actualShifted, "p=" + p);
            assertEquals(expectedHigh, actualHigh, "(high) " + p + ":" + toUnsignedString(expectedHigh) + "," + toUnsignedString(expectedLow)
                    + " <> " + toUnsignedString(actualHigh) + "," + toUnsignedString(actualLow));
            assertEquals(expectedLow, actualLow, "(low) " + p + ":" + toUnsignedString(expectedHigh) + "," + toUnsignedString(expectedLow)
                    + " <> " + toUnsignedString(actualHigh) + "," + toUnsignedString(actualLow));


            value = value.multiply(five);
        }
    }

    /**
     * Tests if the values in {@link FastDoubleMath#MANTISSA_64} and
     * {@link FastDoubleMath#MANTISSA_128} are correct in the range
     * [{@value FastDoubleMath#DOUBLE_MIN_EXPONENT_POWER_OF_TEN},0].
     */
    @Test
    public void testNegativePowersOf10() {
        // Note: We compute powers of 5 here.
        // Since {@literal 10 == 5<<1}, we obtain powers of 10 after we have
        // shifted the value to obtain the 128 most significant bits.
        BigInteger five = new BigInteger(new byte[]{5});
        BigInteger value = BigInteger.ONE;

        // We need enough bits, so that we have a precision of 128 bits for
        // the inverse computed bigOne/Double.DOUBLE_MIN_EXPONENT_POWER_OF_TEN
        BigInteger bigOne = BigInteger.ONE.shiftLeft(-Double.MIN_EXPONENT - 140);


        for (int p = 0; p >= FastDoubleMath.DOUBLE_MIN_EXPONENT_POWER_OF_TEN; p--) {
            long expectedHigh = MANTISSA_64[p - FastDoubleMath.DOUBLE_MIN_EXPONENT_POWER_OF_TEN];
            long expectedLow = MANTISSA_128[p - FastDoubleMath.DOUBLE_MIN_EXPONENT_POWER_OF_TEN];

            byte[] expectedBytes = new byte[17];
            FastDoubleSimd.readLongFromByteArrayBigEndian.set(expectedBytes, 1, expectedHigh);
            FastDoubleSimd.readLongFromByteArrayBigEndian.set(expectedBytes, 1 + 8, expectedLow);
            BigInteger expectedShifted = new BigInteger(expectedBytes);

            BigInteger inverse = bigOne.divide(value);
            int bitLength = inverse.bitLength();
            assertTrue(bitLength >= 128, "we need at least 128 bits of precision: " + bitLength);
            BigInteger actualShifted = inverse.shiftRight(bitLength - 128);
            byte[] actualBytes = actualShifted.toByteArray();
            long actualHigh = (long) FastDoubleSimd.readLongFromByteArrayBigEndian.get(actualBytes, 1);
            long actualLow = (long) FastDoubleSimd.readLongFromByteArrayBigEndian.get(actualBytes, 1 + 8);

            assertEquals(expectedShifted, actualShifted, "p=" + p);
            assertEquals(expectedHigh, actualHigh, "(high) " + p + ":" + toUnsignedString(expectedHigh) + "," + toUnsignedString(expectedLow)
                    + " <> " + toUnsignedString(actualHigh) + "," + toUnsignedString(actualLow));
            assertEquals(expectedLow, actualLow, "(low) " + p + ":" + toUnsignedString(expectedHigh) + "," + toUnsignedString(expectedLow)
                    + " <> " + toUnsignedString(actualHigh) + "," + toUnsignedString(actualLow));

            value = value.multiply(five);
        }
    }
}
