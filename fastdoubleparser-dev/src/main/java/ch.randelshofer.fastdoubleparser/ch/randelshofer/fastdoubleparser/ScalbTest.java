/*
 * @(#)ScalbTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Random;

public class ScalbTest {
    public static void main(String[] args) {
        byte a = -126;
        System.out.println(Integer.toHexString(a << 24 >>> 8) + " " + (a & 0xff));
        Random r = new Random();
        for (int j = 0; j < 100_000_000; j++) {
            double d = r.nextDouble();
            for (int factor = -1; factor > -32; factor--) {
                double x1 = Math.scalb(d, factor);
                double x2 = d * powerOfTwo(factor);
                if (x1 != x2) {
                    System.out.println(x1 + " " + x2 + " " + (x1 - x2));
                }
            }
        }
    }
    public static final int DOUBLE_EXPONENT_BIAS = 1023;
    public static final int DOUBLE_SIGNIFICAND_WIDTH = 53;

    static double powerOfTwo(long exponent) {
        return Double.longBitsToDouble((exponent + DOUBLE_EXPONENT_BIAS) << (DOUBLE_SIGNIFICAND_WIDTH - 1));
    }
}
