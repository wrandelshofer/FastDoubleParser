/*
 * @(#)FastDoubleParserNumericallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastDoubleParserFromCharSequenceNumericallyGeneratedTest extends AbstractDoubleNumericallyGeneratedTest {
    @Override
    protected double parse(String str) {
        return FastDoubleParser.parseDouble(str);
    }
}
