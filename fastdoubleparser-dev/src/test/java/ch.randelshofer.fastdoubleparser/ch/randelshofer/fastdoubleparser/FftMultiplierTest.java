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
    @TestFactory
    public List<DynamicTest> dynamicTestsMultiply() {
        return List.of(
                dynamicTest("'3','0'**84 * '4','0'**84", () -> shouldMultiply(
                        "3" + repeat("0", 84),
                        "4" + repeat("0", 84))),
                dynamicTest("'-','3','0'**84 * '4','0'**84", () -> shouldMultiply(
                        "-3" + repeat("0", 84),
                        "4" + repeat("0", 84))),
                dynamicTest("'-','3','0'**84 * '-','4','0'**84", () -> shouldMultiply(
                        "-3" + repeat("0", 84),
                        "-4" + repeat("0", 84))),
                dynamicTest("'3','0'**100_000 * '4','0'**100_000", () -> shouldMultiply(
                        "3" + repeat("0", 100_000),
                        "4" + repeat("0", 100_000)))
        );

    }

    private void shouldMultiply(String a, String b) {
        BigInteger bigA = new BigInteger(a);
        BigInteger bigB = new BigInteger(b);
        BigInteger expected = bigA.multiply(bigB);
        BigInteger actual = FftMultiplier.multiplyFFT(bigA, bigB);
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


}