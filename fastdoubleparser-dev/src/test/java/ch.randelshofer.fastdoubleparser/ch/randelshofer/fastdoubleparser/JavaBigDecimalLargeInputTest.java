/*
 * @(#)JavaBigDecimalLargeInputTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaBigDecimalLargeInputTest {

    @Test
    public void testLongValidStringFastParse() {
        String value = genLongValidString(500);
        assertEquals(new BigDecimal(value), JavaBigDecimalParser.parseBigDecimal(value.toCharArray()));
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
