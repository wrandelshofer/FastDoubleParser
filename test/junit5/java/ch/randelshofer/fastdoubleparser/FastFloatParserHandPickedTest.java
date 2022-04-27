/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastFloatParserHandPickedTest extends AbstractFloatHandPickedTest {


    @Override
    float parse(CharSequence str) {
        return FastFloatParser.parseFloat(str);
    }

    @Override
    protected float parse(String str, int offset, int length) {
        return FastFloatParser.parseFloat(str, offset, length);
    }
}
