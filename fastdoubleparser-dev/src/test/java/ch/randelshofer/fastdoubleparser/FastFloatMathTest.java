/*
 * @(#)FastFloatMathTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FastFloatMathTest {

    @Test
    void powerOfTwo() {
        for (int i = Float.MIN_EXPONENT; i < Float.MAX_EXPONENT; i++) {
            assertEquals(Math.scalb(1f, i), FastFloatMath.powerOfTwo(false, i));
        }
    }
}