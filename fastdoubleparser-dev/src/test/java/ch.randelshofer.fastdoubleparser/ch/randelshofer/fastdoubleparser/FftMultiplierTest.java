/*
 * @(#)SchoenhageStrassenMultiplierTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigInteger;
import java.util.List;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FftMultiplierTest {
    private boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));

    @TestFactory
    public List<DynamicTest> dynamicTestsMultiply() {
        return List.of(
                dynamicTest("0xFEDCBA9876543210 * 0xEDCBA9876543210", () -> shouldMultiplyFft(
                        new BigInteger("FEDCBA9876543210", 16),
                        new BigInteger("EDCBA9876543210", 16))),
                dynamicTest("3 * 4", () -> shouldMultiplyFft(
                        "3",
                        "4")),
                dynamicTest("2147483647 * 9223372036854775807", () -> shouldMultiplyFft(
                        "2147483647",
                        "9223372036854775807")),
                dynamicTest("'3','0'**84 * '4','0'**84", () -> shouldMultiplyFft(
                        "3" + repeat("0", 84),
                        "4" + repeat("0", 84))),
                dynamicTest("'-','3','0'**84 * '4','0'**84", () -> shouldMultiplyFft(
                        "-3" + repeat("0", 84),
                        "4" + repeat("0", 84))),
                dynamicTest("'-','3','0'**84 * '-','4','0'**84", () -> shouldMultiplyFft(
                        "-3" + repeat("0", 84),
                        "-4" + repeat("0", 84))),
                dynamicTest("'3','0'**100_000 * '4','0'**100_000", () -> shouldMultiplyFft(
                        "3" + repeat("0", 100_000),
                        "4" + repeat("0", 100_000)))
        );
    }

    @TestFactory
    public List<DynamicTest> dynamicLongRunningTestsMultiply() {
        if (longRunningTests) {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return List.of(
                    dynamicTest("1<<Integer.MAX_VALUE/2-1 * 1<<Integer.MAX_VALUE/2-1", () -> shouldMultiplyFft(
                            BigInteger.ONE.shiftLeft(Integer.MAX_VALUE / 2),
                            BigInteger.ONE.shiftLeft(Integer.MAX_VALUE / 2),
                            BigInteger.ONE.shiftLeft(Integer.MAX_VALUE - 1)
                    ))
            );
        } else {
            return List.of();
        }
    }

    @TestFactory
    public List<DynamicTest> dynamicLongRunningTestsSquare() {
        if (longRunningTests) {
            return List.of(
                    dynamicTest("1<<Integer.MAX_VALUE/2 ^2", () -> shouldSquare(
                            BigInteger.ONE.shiftLeft(Integer.MAX_VALUE / 2),
                            BigInteger.ONE.shiftLeft(Integer.MAX_VALUE - 1)
                    ))
            );
        } else {
            return List.of();
        }
    }

    private void shouldMultiplyFft(String strA, String strB) {
        BigInteger a = new BigInteger(strA);
        BigInteger b = new BigInteger(strB);
        BigInteger expected = a.multiply(b);
        BigInteger actual = FftMultiplier.multiplyFft(a, b);
        if (expected.compareTo(actual) != 0) {
            System.err.println("expected: bitLength=" + expected.bitLength());
            System.err.println(toHexString(expected.toByteArray()));
            System.err.println("actual: bitLength=" + actual.bitLength());
            System.err.println(toHexString(actual.toByteArray()));
        }
        assertEquals(expected.compareTo(actual), 0);

    }

    private void shouldMultiplyFft(BigInteger a, BigInteger b) {
        BigInteger expected = a.multiply(b);
        BigInteger actual = FftMultiplier.multiplyFft(a, b);
        if (expected.compareTo(actual) != 0) {
            System.err.println("expected: bitLength=" + expected.bitLength());
            System.err.println(toHexString(expected.toByteArray()));
            System.err.println("actual: bitLength=" + actual.bitLength());
            System.err.println(toHexString(actual.toByteArray()));
        }
        assertEquals(expected.compareTo(actual), 0);

    }

    public static String toHexString(byte[] a) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            String hex = Integer.toHexString(a[i] & 0xff);
            if (hex.length() == 1) {
                b.append('0');
            }
            b.append(hex);
            if (i == iMax) {
                return b.append(']').toString();
            }
            //b.append(", ");
        }
    }

    private void shouldMultiplyFft(BigInteger a, BigInteger b, BigInteger expected) {
        BigInteger actual = FftMultiplier.multiplyFft(a, b);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> dynamicTestsSquare() {
        return List.of(
                dynamicTest("'3','0'**84", () -> shouldSquare(
                        "3" + repeat("0", 84)
                )),
                dynamicTest("'-','3','0'**84", () -> shouldSquare(
                        "-3" + repeat("0", 84)
                )),
                dynamicTest("'-','3','0'**84", () -> shouldSquare(
                        "-3" + repeat("0", 84)
                )),
                dynamicTest("'3','0'**100_000", () -> shouldSquare(
                        "3" + repeat("0", 100_000)
                ))
        );

    }

    private void shouldSquare(String a) {
        BigInteger bigA = new BigInteger(a);
        BigInteger expected = bigA.multiply(bigA);
        BigInteger actual = FftMultiplier.square(bigA);
        assertEquals(expected, actual);
    }

    private void shouldSquare(BigInteger a, BigInteger expected) {
        BigInteger actual = FftMultiplier.square(a);
        assertEquals(expected, actual);
    }


}