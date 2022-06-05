/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

public class FastFloatParserFromByteArrayHandPickedTest extends AbstractFloatHandPickedTest {
    @Override
    float parse(CharSequence str) {
        byte[] bytes = new byte[str.length()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) str.charAt(i);
        }
        return FastFloatParser.parseFloat(bytes);
    }

    @Override
    protected float parse(String str, int offset, int length) {
        byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
        return FastFloatParser.parseFloat(bytes, offset, length);
    }
}
