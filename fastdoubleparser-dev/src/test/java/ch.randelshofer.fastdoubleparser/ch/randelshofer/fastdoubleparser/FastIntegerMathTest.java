/*
 * @(#)FastIntegerMathTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastIntegerMathTest {
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
