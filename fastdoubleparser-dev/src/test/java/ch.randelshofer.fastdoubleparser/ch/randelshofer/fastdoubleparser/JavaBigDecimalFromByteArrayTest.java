/*
 * @(#)JavaBigDecimalFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromByteArrayTest extends AbstractBigDecimalParserTest {


    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parallelParseBigDecimal_byteArray() {
        return createRegularTestData().stream()
                .filter(t -> t.charOffset() == 0 && t.charLength() == t.input().length())
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parallelParseBigDecimal(
                                u.input().toString().getBytes(StandardCharsets.ISO_8859_1)
                        ))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parallelParseBigDecimal_byteArray_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parallelParseBigDecimal(
                                u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parseBigDecimal_byteArray() {
        return createRegularTestData().stream()
                .filter(t -> t.charOffset() == 0 && t.charLength() == t.input().length())
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input().toString().getBytes(StandardCharsets.ISO_8859_1)
                        ))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsJavaBigDecimalParser_parseBigDecimal_byteArray_int_int() {
        return createRegularTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }
}
