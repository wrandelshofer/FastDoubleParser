/*
 * @(#)JavaFloatParserTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toCharArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JavaFloatParser}
 */
public class JavaFloatParserTest extends AbstractJavaFloatParserTest {


    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_CharSequence() {
        return createRegularFloatTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= AbstractFloatValueParser.MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_CharSequence_Int_Int() {
        return createRegularFloatTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_CharSequence_int_int_longRunningTests() {
        ToFloatFunction<NumberTestData> lambda = u -> JavaFloatParser.parseFloat((u.input()), u.charOffset(), u.charLength());
        return Stream.concat(
                getSupplementalTestDataFiles()
                        .map(t -> dynamicTest(t.getFileName().toString(),
                                () -> testFile(t, lambda))),
                createLongRunningFloatTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)))
        );
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_byteArray() {
        return createRegularFloatTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.byteLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t,
                                u -> JavaFloatParser.parseFloat(toByteArray(u.input())
                                ))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_byteArray_int_int() {
        return createRegularFloatTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(toByteArray(u.input()),
                                u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_byteArray_int_int_longRunningTests() {
        ToFloatFunction<NumberTestData> lambda = u -> JavaFloatParser.parseFloat(toByteArray(u.input()), u.charOffset(), u.charLength());
        return Stream.concat(
                getSupplementalTestDataFiles()
                        .map(t -> dynamicTest(t.getFileName().toString(),
                                () -> testFile(t, lambda))),
                createLongRunningFloatTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)))
        );
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_charArray() {
        return createRegularFloatTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charLength() <= EXPECTED_MAX_INPUT_LENGTH
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(toCharArray(u.input())))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_charArray_int_int() {
        return createRegularFloatTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaFloatParser.parseFloat(toCharArray(u.input()),
                                u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTests_parseFloat_charArray_int_int_longRunningTests() {
        ToFloatFunction<NumberTestData> lambda = u -> JavaFloatParser.parseFloat(toCharArray(u.input()), u.charOffset(), u.charLength());
        return Stream.concat(
                getSupplementalTestDataFiles()
                        .map(t -> dynamicTest(t.getFileName().toString(),
                                () -> testFile(t, lambda))),
                createLongRunningFloatTestData()
                        .map(t -> dynamicTest(t.title(),
                                () -> test(t, lambda)))
        );
    }

    protected void testFile(Path path, ToFloatFunction<NumberTestData> f) {
        createSupplementalTestData(path, NumberType.FLOAT32)
                .forEach(d -> test(d, f));
    }

    protected void test(NumberTestData d, ToFloatFunction<NumberTestData> f) {
        if (d.expectedErrorMessage() != null) {
            try {
                float actual = f.applyAsFloat(d);
                fail("should throw an exception but returned " + actual);
            } catch (IllegalArgumentException e) {
                if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                    e.printStackTrace();
                    assertEquals(d.expectedErrorMessage(), e.getMessage());
                }
                assertEquals(d.expectedThrowableClass(), e.getClass());
            }
        } else {
            float actual = f.applyAsFloat(d);
            assertEquals(d.expectedValue().floatValue(), actual, () -> d.input().toString());
        }
    }


    @FunctionalInterface
    public interface ToFloatFunction<T> {

        float applyAsFloat(T value);
    }
}
