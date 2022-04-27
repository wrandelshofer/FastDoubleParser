/*
 * @(#)FastDoubleParserLexcicallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromCharArrayLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {
    protected void testAgainstJdk(String str) {
        double expected = Double.parseDouble(str);
        double actual = FastDoubleParser.parseDouble(str.toCharArray());
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                "longBits of " + expected);
    }
}
