/*
 * @(#)TestFastDoubleParserFromByteArrayVectorized.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromByteArray.tryToParseEightDigitsVectorized;
import static ch.randelshofer.fastdoubleparser.FastDoubleParserFromByteArray.tryToParseEightHexDigitsVectorized;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromByteArrayVectorizedTest extends AbstractFromByteArrayVectorizedTest {


    protected void testTryToParseEightDigits(String str, int expected) {
        int actual = tryToParseEightDigitsVectorized(str.getBytes(StandardCharsets.ISO_8859_1), 0);
        assertEquals(expected, actual);
    }


    protected void dynamicTestsTryToParseEightHexDigits(String str, long expected) {
        long actual = tryToParseEightHexDigitsVectorized(str.getBytes(StandardCharsets.ISO_8859_1), 0);
        assertEquals(expected, actual);
    }

}
