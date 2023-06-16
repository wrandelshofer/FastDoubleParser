/*
 * @(#)JavaBigIntegerFromByteArrayTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigIntegerFromByteArrayTest extends AbstractBigIntegerParserTest {

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_byteArray() {
        return createTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.charLength() == t.input().length()
                            && t.charOffset() == 0
                            && t.radix() == 10;
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(toByteArray(u.input())))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_byteArray_int() {
        return createTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.charLength() == t.input().length()
                            && t.charOffset() == 0;
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(toByteArray(u.input()),
                                u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_byteArray_int_int() {
        return createTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.radix() == 10;
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(toByteArray(u.input()), u.charOffset(), u.charLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_byteArray_int_int_int() {
        return createTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(toByteArray(u.input()), u.charOffset(), u.charLength(), u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_byteArray_int_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(toByteArray(u.input()), u.charOffset(), u.charLength(), u.radix()))));

    }

}
