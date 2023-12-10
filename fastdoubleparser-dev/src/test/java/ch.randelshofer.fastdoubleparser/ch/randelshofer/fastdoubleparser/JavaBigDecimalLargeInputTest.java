/*
 * @(#)JavaBigDecimalLargeInputTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaBigDecimalLargeInputTest {

    private static int LEN = 500;

    @Test
    public void testLongValidInputString() {
        String value = genLongValidString(500);
        assertEquals(new BigDecimal(value), JavaBigDecimalParser.parseBigDecimal(value));
    }

    @Test
    public void testLongValidInputCharArray() {
        assertTrue(ParseDigitsTaskCharArray.RECURSION_THRESHOLD < LEN,
                "LEN is greater than RECURSION_THRESHOLD");
        String value = genLongValidString(500);
        assertEquals(new BigDecimal(value), JavaBigDecimalParser.parseBigDecimal(value.toCharArray()));
    }

    @Test
    public void testLongValidInputByteArray() {
        assertTrue(ParseDigitsTaskByteArray.RECURSION_THRESHOLD < LEN,
                "LEN is greater than RECURSION_THRESHOLD");
        String value = genLongValidString(500);
        assertEquals(new BigDecimal(value),
                JavaBigDecimalParser.parseBigDecimal(value.getBytes(StandardCharsets.ISO_8859_1)));
    }

    static String genLongValidString(int len) {
        final StringBuilder sb = new StringBuilder(len+5);
        sb.append("0.");
        for (int i = 0; i < len; i++) {
            sb.append('0');
        }
        sb.append('1');
        return sb.toString();
    }
}
