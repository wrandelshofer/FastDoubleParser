/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastDoubleParserFromCharArrayHandPickedTest extends AbstractHandPickedTest {
    @Override
    double parse(CharSequence str) {
        char[] chars = new char[str.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = str.charAt(i);
        }
        return FastDoubleParserFromCharArray.parseDouble(chars);
    }
}
