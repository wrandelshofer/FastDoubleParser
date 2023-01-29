/*
 * @(#)JavaBigDecimalFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromCharSequenceTest extends AbstractBigDecimalParserTest {

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_CharSequence() {
        return createRegularTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.byteOffset() == 0 && t.byteLength() == t.input().length();
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input().toString()
                        ))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_CharSequence_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input(),
                                u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_CharSequence_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input(),
                                u.charOffset(), u.charLength()))));
    }

}
