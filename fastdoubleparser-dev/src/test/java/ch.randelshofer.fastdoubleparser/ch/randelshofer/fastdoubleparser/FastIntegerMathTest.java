/*
 * @(#)FastIntegerMathTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastIntegerMathTest {
    @Test
    public void testUnsignedMultiplyHigh() {
        long actualHigh = FastIntegerMath.unsignedMultiplyHigh(0x123456789ABCDEF0L, 0x10L);
        assertEquals(1L, actualHigh);

        actualHigh = FastIntegerMath.unsignedMultiplyHigh(0x123456789ABCDEF0L, -0x10L);
        assertEquals(0x123456789abcdeeeL, actualHigh);
    }
}
