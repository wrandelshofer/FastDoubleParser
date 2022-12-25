/*
 * @(#)JavaBigIntegerFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigIntegerFromByteArrayTest extends AbstractBigIntegerParserTest {
    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_charArray() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0
                        && t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1)))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_charArray_int() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_charArray_int_int() {
        return createTestData().stream()
                .filter(t -> t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1), u.charOffset(), u.charLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_charArray_int_int_int() {
        return createTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1), u.charOffset(), u.charLength(), u.radix()))));

    }


    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_charArray() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0
                        && t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1)))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_charArray_int() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_charArray_int_int() {
        return createTestData().stream()
                .filter(t -> t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1), u.charOffset(), u.charLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_charArray_int_int_int() {
        return createTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1), u.charOffset(), u.charLength(), u.radix()))));

    }
}
