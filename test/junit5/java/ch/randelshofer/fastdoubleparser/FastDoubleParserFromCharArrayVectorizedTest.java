/*
 * @(#)TestFastDoubleParserFromByteArrayVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray.tryToParseEightDigitsSimd;
import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray.tryToParseEightHexDigitsSimd;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromCharArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void testTryToParseEightDigits(String str, long expected) {
        long actual = tryToParseEightDigitsSimd(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }

    protected void testTryToParseSevenDigits(String str, long expected) {
        long result = tryToParseEightDigitsSimd((str + "0").toCharArray(), 0);
        long actual = result == -1L ? -1L : result / 10;
        assertEquals(expected, actual);
    }


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = tryToParseEightHexDigitsSimd(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }

}
