/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastDoubleParserFromCharArrayHandPickedTest extends AbstractDoubleHandPickedTest {
    @Override
    double parse(CharSequence str) {
        char[] chars = new char[str.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = str.charAt(i);
        }
        return FastDoubleParserFromCharArray.parseDouble(chars);
    }

    @Override
    protected double parse(String str, int offset, int length) {
        return FastDoubleParserFromCharArray.parseDouble(str.toCharArray(), offset, length);
    }
}
