/*
 * @(#)FastDoubleSwarTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class FastDoubleSwarTest {
    private static final long EIGHT_ZERO_DIGITS_UTF8 = 0x30_30_30_30_30_30_30_30L;
    private static final long FOUR_ZERO_DIGITS_UTF16 = 0x0030_0030_0030_0030L;
    private static final long ONE_INVALID_DIGIT = 0x40;// ampersand
    private static final long INTERFERENCE_DIGITS = 0x4030300040303080L;

    private static IntStream utf8charPositionInLong() {
        return IntStream.range(1, 9);
    }

    private static long invalidUtf8char(int position) {
        return EIGHT_ZERO_DIGITS_UTF8 | ONE_INVALID_DIGIT << 8 * (8 - position);
    }

    @ParameterizedTest(name = "invalid UTF-8 character at position {0}")
    @MethodSource("utf8charPositionInLong")
    public void countUpToEightDigitsUtf8_invalid(int invalidCharacterPosition) {
        int expected = (invalidCharacterPosition - 1);
        int actual = FastDoubleSwar.countUpToEightDigitsUtf8(invalidUtf8char(invalidCharacterPosition));

        assertEquals(expected, actual, Long.toString(actual, 16));
    }

    @Test
    public void countUpToEightDigitsUtf8_valid() {
        assertEquals(8, FastDoubleSwar.countUpToEightDigitsUtf8(EIGHT_ZERO_DIGITS_UTF8));
    }

    private static IntStream utf16charPositionInLong() {
        return IntStream.range(1, 5);
    }

    private static long invalidUtf16char(int position) {
        return FOUR_ZERO_DIGITS_UTF16 | ONE_INVALID_DIGIT << 16 * (4 - position);
    }

    @ParameterizedTest(name = "invalid UTF-16 character in first chunk at position {0}")
    @MethodSource("utf16charPositionInLong")
    public void countUpToEightDigitsUtf16_firstInvalid(int invalidCharacterPosition) {
        long first = invalidUtf16char(invalidCharacterPosition);
        long second = INTERFERENCE_DIGITS;

        int expected = invalidCharacterPosition - 1;
        int actual = FastDoubleSwar.countUpToEightDigitsUtf16(first, second);

        assertEquals(expected, actual, Long.toString(first, 16));
    }

    @ParameterizedTest(name = "invalid UTF-16 character in second chunk at position {0}")
    @MethodSource("utf16charPositionInLong")
    public void countUpToEightDigitsUtf16_secondInvalid(int invalidCharacterPosition) {
        long first = FOUR_ZERO_DIGITS_UTF16;
        long second = invalidUtf16char(invalidCharacterPosition);

        int expected = 4 + invalidCharacterPosition - 1;
        int actual = FastDoubleSwar.countUpToEightDigitsUtf16(first, second);

        assertEquals(expected, actual, Long.toString(second, 16));
    }

    @Test
    public void countUpToEightDigitsUtf16_bothValid() {
        assertEquals(8, FastDoubleSwar.countUpToEightDigitsUtf16(FOUR_ZERO_DIGITS_UTF16, FOUR_ZERO_DIGITS_UTF16));
    }
}