/*
 * @(#)JavaBigIntegerFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigIntegerFromCharSequenceTest extends AbstractBigIntegerParserTest {

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_CharSequence() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0
                        && t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_CharSequence_int() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(),
                                u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_CharSequence_int_int() {
        return createTestData().stream()
                .filter(t -> t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(), u.charOffset(), u.charLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parseBigInteger_CharSequence_int_int_int() {
        return createTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(), u.charOffset(), u.charLength(), u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_CharSequence() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0
                        && t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_CharSequence_int() {
        return createTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input(),
                                u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_CharSequence_int_int() {
        return createTestData().stream()
                .filter(t -> t.radix() == 10)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input(), u.charOffset(), u.charLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParse_JavaBigIntegerParser_parallelParseBigInteger_CharSequence_int_int_int() {
        return createTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input(), u.charOffset(), u.charLength(), u.radix()))));

    }
}
