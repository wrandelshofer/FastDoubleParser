/*
 * @(#)EightDigitsSwarTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.EightDigitsTestDataFactory.createLegalEightDecDigitsLiterals;
import static ch.randelshofer.fastdoubleparser.EightDigitsTestDataFactory.createLegalEightHexDigitsLiterals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class EightDigitsSwarTest {
    @TestFactory
    public Stream<DynamicTest> dynamicTests_Dec() {
        return Stream.concat(
                        EightDigitsTestDataFactory.createIllegalEightDecDigitsLiterals().stream(),
                        createLegalEightDecDigitsLiterals().stream()
                )
                .filter(s -> {
                    NumberTestData t = s;
                    return t.charOffset() == 0 && t.charLength() == t.input().length();
                })
                .map(t -> dynamicTest(t.title(),
                        () -> testDec(t)
                ));

    }

    public void testDec(NumberTestData t) {
        testDecUtf16(t.input().toString(), t.charOffset(), t.expectedValue());
        testDecUtf8(t.input().toString(), t.charOffset(), t.expectedValue());
    }

    private static void testDecUtf8(String s, int offset, Number expected) {
        int actual;
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        actual = FastDoubleSwar.tryToParseEightDigitsUtf8(bytes, offset);
        assertEquals(expected == null ? -1 : expected, actual);

        long value = ((bytes[offset + 7] & 0xffL) << 56)
                | ((bytes[offset + 6] & 0xffL) << 48)
                | ((bytes[offset + 5] & 0xffL) << 40)
                | ((bytes[offset + 4] & 0xffL) << 32)
                | ((bytes[offset + 3] & 0xffL) << 24)
                | ((bytes[offset + 2] & 0xffL) << 16)
                | ((bytes[offset + 1] & 0xffL) << 8)
                | (bytes[offset] & 0xffL);

        int result;
        long val = value - 0x3030303030303030L;
        long det = ((value + 0x4646464646464646L) | val) &
                0x8080808080808080L;
        if (det != 0L) {
            result = -1;
        } else {// The last 2 multiplications in this algorithm are independent of each
// other.
            long mask = 0x000000FF_000000FFL;
            val = (val * 0xa_01L) >>> 8;// 1+(10<<8)
            val = (((val & mask) * 0x000F4240_00000064L)//100 + (1000000 << 32)
                    + (((val >>> 16) & mask) * 0x00002710_00000001L)) >>> 32;// 1 + (10000 << 32)
            result = (int) val;
        }

        actual = result;
        assertEquals(expected == null ? -1 : expected, actual);
    }

    private static void testDecUtf16(String s, int offset, Number expected) {
        char[] chars = s.toCharArray();
        int actual = FastDoubleSwar.tryToParseEightDigits(chars, offset);
        assertEquals(expected == null ? -1 : expected, actual);
        long first = chars[offset + 0] | ((long) chars[offset + 1] << 16) | ((long) chars[offset + 2] << 32) | ((long) chars[offset + 3] << 48);
        long second = chars[offset + 4] | ((long) chars[offset + 5] << 16) | ((long) chars[offset + 6] << 32) | ((long) chars[offset + 7] << 48);
        actual = FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
        assertEquals(expected == null ? -1 : expected, actual);
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_Hex() {
        return Stream.concat(
                        EightDigitsTestDataFactory.createIllegalEightHexDigitsLiterals().stream(),
                        createLegalEightHexDigitsLiterals().stream()
                )
                .filter(s -> {
                    NumberTestData t = s;
                    return t.charOffset() == 0 && t.charLength() == t.input().length();
                })
                .map(t -> dynamicTest(t.title(),
                        () -> testHex(t)));

    }

    public void testHex(NumberTestData t) {
        testHexString(t.input().toString(), t.charOffset(), t.expectedValue() == null ? -1L : t.expectedValue().longValue());
        testHexChar(t.input().toString().toCharArray(), t.charOffset(), t.expectedValue() == null ? -1L : t.expectedValue().longValue());
        testHexByte(t.input().toString().getBytes(StandardCharsets.UTF_8), t.charOffset(), t.expectedValue() == null ? -1L : t.expectedValue().longValue());
    }

    public void testHexString(String s, int offset, long expected) {
        long actual = FastDoubleSwar.tryToParseEightHexDigits(s, offset);
        if (expected < 0) {
            assertTrue(actual < 0);
        } else {
            assertEquals(expected, actual);
        }
        char[] chars = s.toCharArray();
        actual = FastDoubleSwar.tryToParseEightHexDigits(chars, offset);
        if (expected < 0) {
            assertTrue(actual < 0);
        } else {
            assertEquals(expected, actual);
        }

        long first = (long) chars[offset] << 48
                | (long) chars[offset + 1] << 32
                | (long) chars[offset + 2] << 16
                | (long) chars[offset + 3];

        long second = (long) chars[offset + 4] << 48
                | (long) chars[offset + 5] << 32
                | (long) chars[offset + 6] << 16
                | (long) chars[offset + 7];
        actual = FastDoubleSwar.tryToParseEightHexDigitsUtf16(first, second);
        if (expected < 0) {
            assertTrue(actual < 0);
        } else {
            assertEquals(expected, actual);
        }

    }


    void testHexByte(byte[] b, int offset, long expected) {
        long actual = FastDoubleSwar.tryToParseEightHexDigits(b, offset);
        if (expected < 0) {
            assertTrue(actual < 0);
        } else {
            assertEquals(expected, actual);
        }
    }


    void testHexChar(char[] b, int offset, long expected) {
        long actual = FastDoubleSwar.tryToParseEightHexDigits(b, offset);
        if (expected < 0) {
            assertTrue(actual < 0);
        } else {
            assertEquals(expected, actual);
        }
    }
}
