/*
 * @(#)FastDoubleMathTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static ch.randelshofer.fastdoubleparser.FastDoubleMath.MANTISSA_128;
import static ch.randelshofer.fastdoubleparser.FastDoubleMath.MANTISSA_64;
import static java.lang.Long.toUnsignedString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link FastDoubleMath}.
 */
public class FastDoubleMathTest {


    @TestFactory
    public List<DynamicNode> dynamicTestsTryDecFloatToDouble() {
        return Arrays.asList(
                dynamicTest("Inside Clinger fast path (max_clinger_significand, max_clinger_exponent), 9007199254740991e22Inside Clinger fast path (max_clinger_significand, max_clinger_exponent), 9007199254740991e22", () -> testTryDecFloatToDouble(false, 9007199254740991L, 22, 9007199254740991e22)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", () -> testTryDecFloatToDouble(false, 9007199254740991L, 23, 9007199254740991e23)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", () -> testTryDecFloatToDouble(false, 9007199254740992L, 22, 9007199254740992e22)),
                dynamicTest("Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", () -> testTryDecFloatToDouble(false, 1L, -22, 1e-22)),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testTryDecFloatToDouble(false, 1L, -23, 1e-23)),
                dynamicTest("Outside Clinger fast path, bail out in semi-fast path, -8446744073709551617", () -> testTryDecFloatToDouble(false, -8446744073709551617L, 0, Double.NaN)),
                dynamicTest("Outside Clinger fast path, semi-fast path, -9223372036854775808e7", () -> testTryDecFloatToDouble(false, -9223372036854775808L, 7, 9.223372036854776E25)),
                dynamicTest("Outside Clinger fast path, semi-fast path, exponent out of range, -9223372036854775808e-325", () -> testTryDecFloatToDouble(false, -9223372036854775808L, -325, 9.223372036854776E-307)),
                dynamicTest("Outside Clinger fast path, bail-out in semi-fast path, 1e23", () -> testTryDecFloatToDouble(false, 1L, 23, Double.NaN)),
                dynamicTest("Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16", () -> testTryDecFloatToDouble(false, 72057594037927933L, 0, 7.205759403792794E16)),
                dynamicTest("Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15", () -> testTryDecFloatToDouble(false, 73177701707893310L, -1, Double.NaN))
        );
    }

    public void testTryDecFloatToDouble(boolean isNegative, long significand, int power, double expected) {
        double actual = FastDoubleMath.tryDecToDoubleWithFastAlgorithm(isNegative, significand, power);
        assertEquals(expected, actual);
    }


    @TestFactory
    public List<DynamicNode> dynamicTestsTryHexFloatToDouble() {
        return Arrays.asList(
                dynamicTest("Significand high-bit clear \"0x7000000000000000p3\")", () -> testTryHexFloatToDouble(false, 0x7000000000000000L, 3, 0x7000000000000000p3)),
                dynamicTest("Significand high-bit set \"0x8000000000000000p3\")", () -> testTryHexFloatToDouble(false, 0x8000000000000000L, 3, 0x8000000000000000p3)),
                dynamicTest("Significand high-bit set \"0x8f00000000000000p3\")", () -> testTryHexFloatToDouble(false, 0x8f00000000000000L, 3, 0x8f00000000000000p3)),
                dynamicTest("Significand negative \"-0x5p7\")", () -> testTryHexFloatToDouble(true, 0x5L, 7, -0x5p7)),
                dynamicTest("Exponent negative \"0x5p-7\")", () -> testTryHexFloatToDouble(false, 0x5L, -7, 0x5p-7))
        );
    }

    public void testTryHexFloatToDouble(boolean isNegative, long significand, int power, double expected) {
        double actual = FastDoubleMath.tryHexFloatToDoubleTruncated(isNegative, significand, power, false, power);
        assertEquals(expected, actual);
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
            FastDoubleSwar.writeLongBE(expectedBytes, 1, expectedHigh);
            FastDoubleSwar.writeLongBE(expectedBytes, 1 + 8, expectedLow);
            BigInteger expectedShifted = new BigInteger(expectedBytes);

            BigInteger inverse = bigOne.divide(value);
            int bitLength = inverse.bitLength();
            assertTrue(bitLength >= 128, "we need at least 128 bits of precision: " + bitLength);
            BigInteger actualShifted = inverse.shiftRight(bitLength - 128);
            byte[] actualBytes = actualShifted.toByteArray();
            long actualHigh = (long) FastDoubleSwar.readLongBE(actualBytes, 1);
            long actualLow = (long) FastDoubleSwar.readLongBE(actualBytes, 1 + 8);

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
            FastDoubleSwar.writeLongBE(expectedBytes, 1, expectedHigh);
            FastDoubleSwar.writeLongBE(expectedBytes, 1 + 8, expectedLow);
            BigInteger expectedShifted = new BigInteger(expectedBytes);

            int bitLength = value.bitLength();
            BigInteger actualShifted = bitLength <= 128 ? value.shiftLeft(128 - bitLength) : value.shiftRight(bitLength - 128);
            byte[] actualBytes = actualShifted.toByteArray();
            long actualHigh = (long) FastDoubleSwar.readLongBE(actualBytes, 1);
            long actualLow = (long) FastDoubleSwar.readLongBE(actualBytes, 1 + 8);

            assertEquals(expectedShifted, actualShifted, "p=" + p);
            assertEquals(expectedHigh, actualHigh, "(high) " + p + ":" + toUnsignedString(expectedHigh) + "," + toUnsignedString(expectedLow)
                    + " <> " + toUnsignedString(actualHigh) + "," + toUnsignedString(actualLow));
            assertEquals(expectedLow, actualLow, "(low) " + p + ":" + toUnsignedString(expectedHigh) + "," + toUnsignedString(expectedLow)
                    + " <> " + toUnsignedString(actualHigh) + "," + toUnsignedString(actualLow));


            value = value.multiply(five);
        }
    }

    @TestFactory
    public List<DynamicNode> dynamicTestsFastScalb() {
        return Arrays.asList(
                dynamicTest("3, 5", () -> testFastScalb(3, 5)),
                dynamicTest("3, -5", () -> testFastScalb(3, -5)),
                dynamicTest("-3, 5", () -> testFastScalb(-3, 5)),
                dynamicTest("-3, -5", () -> testFastScalb(-3, -5)),
                dynamicTest("min number, min scaleFactor", () -> testFastScalb(Double.MIN_VALUE, Double.MIN_EXPONENT)),
                dynamicTest("min number, max scaleFactor", () -> testFastScalb(Double.MIN_VALUE, Double.MAX_EXPONENT)),
                dynamicTest("max number, min scaleFactor", () -> testFastScalb(Double.MAX_VALUE, Double.MIN_EXPONENT)),
                dynamicTest("max number, min scaleFactor", () -> testFastScalb(Double.MAX_VALUE, Double.MAX_EXPONENT))
        );
    }

    void testFastScalb(double number, int scaleFactor) {
        assertEquals(Math.scalb(number, scaleFactor), FastDoubleMath.fastScalb(number, scaleFactor));
    }
}
