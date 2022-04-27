/*
 * @(#)FastDoubleParserNumericallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastDoubleParserFromCharArrayNumericallyGeneratedTest extends AbstractDoubleNumericallyGeneratedTest {
    @Override
    protected double parse(String str) {
        return FastDoubleParserFromCharArray.parseDouble(str.toCharArray());
    }
}
