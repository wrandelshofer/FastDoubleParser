/*
 * @(#)TestFastDoubleParserFromByteArrayVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray.tryToParseEightDigitsVectorized;
import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray.tryToParseEightHexDigitsVectorized;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromCharArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void testTryToParseEightDigits(String str, int expected) {
        int actual = tryToParseEightDigitsVectorized(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = tryToParseEightHexDigitsVectorized(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }

}
