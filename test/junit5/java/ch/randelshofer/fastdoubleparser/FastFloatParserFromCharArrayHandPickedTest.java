/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastFloatParserFromCharArrayHandPickedTest extends AbstractFloatHandPickedTest {
    @Override
    float parse(CharSequence str) {
        char[] chars = new char[str.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = str.charAt(i);
        }
        return FastFloatParser.parseFloat(chars);
    }

    @Override
    protected float parse(String str, int offset, int length) {
        return FastFloatParser.parseFloat(str.toCharArray(), offset, length);
    }
}
