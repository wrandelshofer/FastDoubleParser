/*
 * @(#)FastFloatMathTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link FastFloatMath}.
 */
public class FastFloatMathTest {

    @Test
    public void bruteForce() {
        for (int e = 0; e < Integer.MAX_VALUE; e++) {
            try {
                testTryDecFloatToFloat(false, e + 16777215L, 0, Float.NaN);
                System.out.println("e:" + (e + 16777215L));
                return;
            } catch (AssertionError ex) {
            }
        }
    }

    @TestFactory
    public List<DynamicNode> dynamicTestsTryDecFloatToFloat() {
        return Arrays.asList(
                dynamicTest("Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", () -> testTryDecFloatToFloat(false, 16777215L, 10, 16777215e10f)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", () -> testTryDecFloatToFloat(false, 16777215L, 11, 16777215e11f)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", () -> testTryDecFloatToFloat(false, 16777216L, 10, 16777216e10f)),
                dynamicTest("Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", () -> testTryDecFloatToFloat(false, 1L, -10, 1e-10f)),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testTryDecFloatToFloat(false, 1L, -11, 1e-11f)),
                dynamicTest("Outside Clinger fast path, bail out in semi-fast path, 16777217", () -> testTryDecFloatToFloat(false, 16777217L, 0, Float.NaN)),
                dynamicTest("Outside Clinger fast path, semi-fast path, -9223372036854775808e7", () -> testTryDecFloatToFloat(false, -9223372036854775808L, 7, 9.223372036854776E25f)),
                dynamicTest("Outside Clinger fast path, semi-fast path, exponent out of range, -9223372036854775808e-45", () -> testTryDecFloatToFloat(false, -9223372036854775808L, -45, 9.223372036854776E-27f)),
                dynamicTest("Outside Clinger fast path, bail-out in semi-fast path, 1e39", () -> testTryDecFloatToFloat(false, 1L, 39, Float.NaN)),
                dynamicTest("Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16", () -> testTryDecFloatToFloat(false, 72057594037927933L, 0, 7.205759403792794E16f))
        );
    }

    public void testTryDecFloatToFloat(boolean isNegative, long significand, int power, float expected) {
        float actual = FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand, power);
        assertEquals(expected, actual);
    }


    @TestFactory
    public List<DynamicNode> dynamicTestsTryHexFloatToFloat() {
        return Arrays.asList(
                dynamicTest("Significand high-bit clear \"0x7000000000000000p3\")", () -> testTryHexFloatToFloat(false, 0x7000000000000000L, 3, 0x7000000000000000p3f)),
                dynamicTest("Significand high-bit set \"0x8000000000000000p3\")", () -> testTryHexFloatToFloat(false, 0x8000000000000000L, 3, 0x8000000000000000p3f)),
                dynamicTest("Significand high-bit set \"0x8f00000000000000p3\")", () -> testTryHexFloatToFloat(false, 0x8f00000000000000L, 3, 0x8f00000000000000p3f)),
                dynamicTest("Significand negative \"-0x5p7\")", () -> testTryHexFloatToFloat(true, 0x5L, 7, -0x5p7f)),
                dynamicTest("Exponent negative \"0x5p-7\")", () -> testTryHexFloatToFloat(false, 0x5L, -7, 0x5p-7f))
        );
    }

    public void testTryHexFloatToFloat(boolean isNegative, long significand, int power, float expected) {
        float actual = FastFloatMath.tryHexFloatToFloatTruncated(isNegative, significand, power, false, power);
        assertEquals(expected, actual);
    }

    @Test
    void scalb() {
        Random r = new Random(0);
        for (int j = 0; j < 1000; j++) {
            float f = r.nextFloat();
            for (int i = Float.MIN_EXPONENT; i < Float.MAX_EXPONENT; i++) {
                assertEquals(Math.scalb(f, i), JmhScalb.customScalbInt(f, i));
            }
        }
    }
}
