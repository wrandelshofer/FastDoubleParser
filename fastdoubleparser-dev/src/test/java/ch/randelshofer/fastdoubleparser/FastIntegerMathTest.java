/*
 * @(#)FastIntegerMathTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static Stream<Arguments> splitFloor16testData() {
        return Stream.of(// from, to, expectedMid
                Arguments.of(0, 0, 0),
                Arguments.of(0, 16, 0),
                Arguments.of(10, 30, 14),
                Arguments.of(0, 32, 16),
                Arguments.of(10, 40, 24),
                Arguments.of(0, 100, 36),
                Arguments.of(1, 101, 37)
        );
    }

    @ParameterizedTest
    @MethodSource("splitFloor16testData")
    void splitFloor16specialValues(int from, int to, int expectedMid) {
        assert (to - expectedMid) % 16 == 0;
        assert expectedMid <= from + (to - from) / 2;
        assert expectedMid >= from + (to - from) / 2 - 15;

        assertEquals(expectedMid, FastIntegerMath.splitFloor16(to - from, to));
    }

    @Test
    void splitFloor16() {
        for (int from = 0; from < 50; from++) {
            for (int to = from; to < 100; to++) {
                int actual = FastIntegerMath.splitFloor16(to - from, to);
                assertEquals(0, (to - actual) % 16);
                assertTrue(actual <= from + (to - from) / 2);
                assertTrue(actual >= from + (to - from) / 2 - 15);

                assertEquals(oldSplitFloor16(from, to), actual);
            }
        }
    }

    static int oldSplitFloor16(int from, int to) {
        int mid = (from + to) >>> 1;// split in half
        mid = to - (((to - mid + 15) >> 4) << 4);// make numDigits of low a multiple of 16
        return mid;
    }

}
