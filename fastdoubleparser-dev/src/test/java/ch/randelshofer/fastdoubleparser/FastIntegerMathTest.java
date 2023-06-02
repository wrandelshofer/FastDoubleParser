/*
 * @(#)FastIntegerMathTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastIntegerMathTest {

    @Test
    void estimateNumBitsSmallValues() {
        for (int i = 0; i < 500; i++) { // works for i < 119185
            long expected = digitsToBitsSlow(i);
            long actual = FastIntegerMath.estimateNumBits(i);
            assertEquals(actual, expected);
        }
    }

    @Test
    void estimateNumBitsRandomValues() {
        Random r = new Random(0);
        for (int i = 0; i < 1000; i++) {
            int x = r.nextInt(Integer.MAX_VALUE); // random positive value
            long expected = digitsToBits(x);
            long actual = FastIntegerMath.estimateNumBits(x);
            assertEqualsOrLessByOne(actual, expected);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {119185, 293637, 336249, 378861, 421473})
    void estimateNumBitsInaccurateValues(int inaccurate) {
        assertEquals(digitsToBits(inaccurate) + 1, FastIntegerMath.estimateNumBits(inaccurate));
    }

    @Test
    @DisabledIfSystemProperty(named = "enableLongRunningTests", matches = "^false$")
    void estimateNumBitsAll() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            long expected = digitsToBits(i);
            long actual = FastIntegerMath.estimateNumBits(i);
            assertEqualsOrLessByOne(actual, expected);
        }
    }

    private static void assertEqualsOrLessByOne(long actual, long expected) { // actual >= expected
        assertTrue(actual - expected >= 0,actual + " - " + expected + " < 0");
        assertTrue(actual - expected <= 1, actual + " - " + expected + " > 1");
    }

    // supposed to be precisely accurate
    private static long digitsToBits(int numDecimalDigits) {
        return (long)(3.32192809488736234787 * numDecimalDigits) + 1;
    }

    // precisely accurate, but slow
    private static long digitsToBitsSlow(int numDecimalDigits) {
        return numDecimalDigits + BigInteger.valueOf(5).pow(numDecimalDigits).bitLength();
    }

    @Test
    public void testFullMultiplication() {
        FastIntegerMath.UInt128 actual = FastIntegerMath.fullMultiplication(0x123456789ABCDEF0L, 0x10L);
        assertEquals(1L, actual.high);
        assertEquals(0x23456789abcdef00L, actual.low);

        actual = FastIntegerMath.fullMultiplication(0x123456789ABCDEF0L, -0x10L);
        assertEquals(0x123456789abcdeeeL, actual.high);
        assertEquals(0xdcba987654321100L, actual.low);
    }
}
