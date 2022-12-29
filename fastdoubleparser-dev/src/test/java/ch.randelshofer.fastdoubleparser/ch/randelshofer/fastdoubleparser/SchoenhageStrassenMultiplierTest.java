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

public class SchoenhageStrassenMultiplierTest {
    @TestFactory
    public List<DynamicTest> dynamicTests() {
        return List.of(
                dynamicTest("'3' '0'**84 * '4' '0'**84", () -> test(
                        "3" + repeat("0", 84),
                        "4" + repeat("0", 84)))
        );

    }

    private void test(String a, String b) {
        BigInteger bigA = new BigInteger(a);
        BigInteger bigB = new BigInteger(b);
        BigInteger expected = bigA.multiply(bigB);
        BigInteger actual = SchoenhageStrassenMultiplier.multiplySchoenhageStrassen(bigA, bigB, false);
        assertEquals(expected, actual);
    }


}