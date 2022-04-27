/*
 * @(#)FastDoubleParserLexcicallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastFloatParserFromByteSequenceLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {
    protected void testAgainstJdk(String str) {
        float expected = Float.parseFloat(str);
        float actual = new FloatFromCharSequenceParser().parse(str);
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Float.floatToIntBits(expected), Float.floatToIntBits(actual),
                "intBits of " + expected);
    }

}
