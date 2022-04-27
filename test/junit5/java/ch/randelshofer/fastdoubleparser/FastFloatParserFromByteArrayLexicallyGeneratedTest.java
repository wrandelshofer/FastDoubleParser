/*
 * @(#)FastDoubleParserLexcicallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastFloatParserFromByteArrayLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {
    protected void testAgainstJdk(String str) {
        float expected = Float.parseFloat(str);
        float actual = FastFloatParser.parseFloat(str.getBytes(StandardCharsets.ISO_8859_1));
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Float.floatToIntBits(expected), Float.floatToIntBits(actual),
                "intBits of " + expected);
    }

}
