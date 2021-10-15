/*
 * @(#)FastDoubleParserLexcicallyGeneratedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

public class FastDoubleParserLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {
    @Override
    protected double parse(String str) {
        return FastDoubleParser.parseDouble(str);
    }
}
