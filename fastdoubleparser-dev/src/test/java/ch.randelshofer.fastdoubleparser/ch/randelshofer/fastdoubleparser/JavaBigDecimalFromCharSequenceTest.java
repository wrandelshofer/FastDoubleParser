/*
 * @(#)JavaBigDecimalFromCharSequenceTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromCharSequenceTest extends AbstractBigDecimalParserTest {


    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parallelParseBigDecimal_CharSequence() {
        return createRegularTestData().stream()
                .filter(t -> t.charOffset() == 0 && t.charLength() == t.input().length())
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parallelParseBigDecimal(
                                u.input().toString()
                        ))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parallelParseBigDecimal_CharSequence_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parallelParseBigDecimal(
                                u.input().toString(),
                                u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parseBigDecimal_CharSequence() {
        return createRegularTestData().stream()
                .filter(t -> t.byteOffset() == 0 && t.byteLength() == t.input().length())
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input().toString()
                        ))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parseBigDecimal_CharSequence_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input().toString(),
                                u.charOffset(), u.charLength()))));
    }
}
