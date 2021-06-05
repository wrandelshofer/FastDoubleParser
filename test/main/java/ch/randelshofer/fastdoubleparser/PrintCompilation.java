/*
 * @(#)DumpCompilation.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Run this class with the following VM options:
 * <pre>
 * -Xbatch -XX:-TieredCompilation -XX:+PrintCompilation
 * -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
 * </pre>
 */
public class PrintCompilation {
    public static double measureFastDoubleParser17DigitsWith3DigitExp() {
        String str = "123.45678901234567e123";
        return FastDoubleParser.parseDouble(str);
    }

    public static void main(String... args) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 100_000; i++) {
            max = Math.max(max, measureFastDoubleParser17DigitsWith3DigitExp());
        }
        System.out.println("max:" + max);
    }
}
