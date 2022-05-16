/*
 * @(#)TestFastDoubleParserVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromCharArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = FastDoubleVector.tryToParseEightHexDigitsUtf16(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }

    protected void testTryToParseEightDigits(String str, long expected) {
        long actual = FastDoubleVector.tryToParseEightDigitsUtf16(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }

    protected void testTryToParseSevenDigits(String str, long expected) {
        long result = FastDoubleVector.tryToParseEightDigitsUtf16((str + "0").toCharArray(), 0);
        long actual = result == -1L ? -1L : result / 10;
        assertEquals(expected, actual);
    }

}
