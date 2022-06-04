/*
 * @(#)FastDoubleParserLexcicallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastDoubleParserFromCharArrayLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {
    protected void testAgainstJdk(String str) {
        double expected = 0.0;
        boolean isExpectedToFail = false;
        try {
            expected = Double.parseDouble(str);
        } catch (NumberFormatException t) {
            isExpectedToFail = true;
        }

        double actual = 0;
        boolean actualFailed = false;
        try {
            actual = FastDoubleParser.parseDouble(str.toCharArray());
        } catch (NumberFormatException t) {
            actualFailed = true;
        }

        assertEquals(isExpectedToFail, actualFailed);
        if (!isExpectedToFail) {
            assertEquals(expected, actual, "str=" + str);
            assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                    "longBits of " + expected);
        }
    }
}
