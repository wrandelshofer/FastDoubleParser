/*
 * @(#)TestFastDoubleParserVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromByteArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void testTryToParseEightDigits(String str, long expected) {
        long actual = FastDoubleSimd.tryToParseEightDigitsUtf8Vector(str.getBytes(StandardCharsets.ISO_8859_1), 0);
        assertEquals(expected, actual);
    }

    protected void testTryToParseSevenDigits(String str, long expected) {
        long result = FastDoubleSimd.tryToParseEightDigitsUtf8Vector((str + "0").getBytes(StandardCharsets.ISO_8859_1), 0);
        long actual = result == -1L ? -1L : result / 10;
        assertEquals(expected, actual);
    }


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = FastDoubleSimd.tryToParseEightHexDigitsUtf8Vector(str.getBytes(StandardCharsets.ISO_8859_1), 0);
        assertEquals(expected, actual);
    }

}