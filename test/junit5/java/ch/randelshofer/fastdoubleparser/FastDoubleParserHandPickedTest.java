/*
 * @(#)FastDoubleParserHandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastDoubleParserHandPickedTest extends AbstractHandPickedTest {


    @Override
    double parse(CharSequence str) {
        return FastDoubleParser.parseDouble(str);
    }

    @Override
    protected double parse(String str, int offset, int length) {
        return FastDoubleParser.parseDouble(str, offset, length);
    }
}
