/*
 * @(#)TestFastDoubleParserFromByteArrayVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray.tryToParseEightDigits;
import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromCharArray.tryToParseEightHexDigits;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromCharArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void testTryToParseEightDigits(String str, long expected) {
        long actual = tryToParseEightDigits(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = tryToParseEightHexDigits(str.toCharArray(), 0);
        assertEquals(expected, actual);
    }

}
