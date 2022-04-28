/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

public class FastDoubleParserFromByteArrayHandPickedTest extends AbstractDoubleHandPickedTest {
    @Override
    double parse(CharSequence str) {
        byte[] bytes = new byte[str.length()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) str.charAt(i);
        }
        return FastDoubleParser.parseDouble(bytes);
    }

    @Override
    protected double parse(String str, int offset, int length) {
        byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
        return FastDoubleParser.parseDouble(bytes, offset, length);
    }
}
