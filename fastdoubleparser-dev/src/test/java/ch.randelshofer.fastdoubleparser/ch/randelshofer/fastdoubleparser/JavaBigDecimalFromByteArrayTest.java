/*
 * @(#)JavaBigDecimalFromByteArrayTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromByteArrayTest extends AbstractBigDecimalParserTest {


    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_byteArray() {
        return createRegularTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.charOffset() == 0 && t.charLength() == t.input().length();
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                toByteArray(u.input())
                        ))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_byteArray_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                toByteArray(u.input()),
                                u.byteOffset(), u.byteLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_byteArray_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                toByteArray(u.input()),
                                u.byteOffset(), u.byteLength()))));

    }
}
