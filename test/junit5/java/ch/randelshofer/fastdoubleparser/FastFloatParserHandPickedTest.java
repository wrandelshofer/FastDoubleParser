/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastFloatParserHandPickedTest extends AbstractFloatHandPickedTest {


    @Override
    float parse(CharSequence str) {
        return FastDoubleParser.parseFloat(str);
    }

    @Override
    protected float parse(String str, int offset, int length) {
        return FastDoubleParser.parseFloat(str, offset, length);
    }
}
