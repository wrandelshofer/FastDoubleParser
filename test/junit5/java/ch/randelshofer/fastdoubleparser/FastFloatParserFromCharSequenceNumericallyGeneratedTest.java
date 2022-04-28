/*
 * @(#)FastDoubleParserNumericallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastFloatParserFromCharSequenceNumericallyGeneratedTest extends AbstractFloatNumericallyGeneratedTest {
    @Override
    protected float parse(String str) {
        return FastFloatParser.parseFloat(str);
    }
}
