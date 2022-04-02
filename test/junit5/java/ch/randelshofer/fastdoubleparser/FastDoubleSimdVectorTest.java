/*
 * @(#)FastDoubleSimdUtf16VectorTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleSimdVectorTest extends AbstractFastDoubleSimdTest {
    @Override
    void testDec(String s, int offset, int expected) {
        char[] chars = s.toCharArray();

        int actual = FastDoubleSimd.tryToParseEightDigitsUtf16Vector(chars, offset);
        assertEquals(expected, actual);


        long first = chars[offset + 0] | ((long) chars[offset + 1] << 16) | ((long) chars[offset + 2] << 32) | ((long) chars[offset + 3] << 48);
        long second = chars[offset + 4] | ((long) chars[offset + 5] << 16) | ((long) chars[offset + 6] << 32) | ((long) chars[offset + 7] << 48);
        actual = FastDoubleSimd.tryToParseEightDigitsUtf16Vector(first, second);
        assertEquals(expected, actual);


    }

    @Override
    void testHex(String s, int offset, long expected) {
        long actual = FastDoubleSimd.tryToParseEightHexDigitsUtf16Vector(s.toCharArray(), offset);
        assertEquals(expected, actual);

        actual = FastDoubleSimd.tryToParseEightHexDigitsUtf8Vector(s.getBytes(StandardCharsets.UTF_8), offset);
        assertEquals(expected, actual);
    }
}
