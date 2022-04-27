/*
 * @(#)FastDoubleParserLexcicallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromByteArrayLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {
    protected void testAgainstJdk(String str) {
        double expected = Double.parseDouble(str);
        double actual = FastDoubleParserFromByteArray.parseDouble(str.getBytes(StandardCharsets.ISO_8859_1));
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                "longBits of " + expected);
    }

}
