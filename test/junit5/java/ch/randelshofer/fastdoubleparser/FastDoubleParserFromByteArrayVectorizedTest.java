/*
 * @(#)TestFastDoubleParserVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromByteArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = FastDoubleVector.tryToParseEightHexDigitsUtf8(str.getBytes(StandardCharsets.ISO_8859_1), 0);
        assertEquals(expected, actual);
    }

    protected void testTryToParseEightDigits(String str, long expected) {
        long actual = FastDoubleVector.tryToParseEightDigitsUtf8(str.getBytes(StandardCharsets.ISO_8859_1), 0);
        assertEquals(expected, actual);
    }

    protected void testTryToParseSevenDigits(String str, long expected) {
        long result = FastDoubleVector.tryToParseEightDigitsUtf8((str + "0").getBytes(StandardCharsets.ISO_8859_1), 0);
        long actual = result == -1L ? -1L : result / 10;
        assertEquals(expected, actual);
    }

}
