/*
 * @(#)FastIntegerMathTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastIntegerMathTest {
    @Test
    public void testUnsignedMultiplyHigh() {
        long actualHigh = FastIntegerMath.unsignedMultiplyHigh(0x123456789ABCDEF0L, 0x10L);
        assertEquals(1L, actualHigh);

        actualHigh = FastIntegerMath.unsignedMultiplyHigh(0x123456789ABCDEF0L, -0x10L);
        assertEquals(0x123456789abcdeeeL, actualHigh);
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

        assertEquals(expectedMid, FastIntegerMath.splitFloor16(from, to));
    }
}
