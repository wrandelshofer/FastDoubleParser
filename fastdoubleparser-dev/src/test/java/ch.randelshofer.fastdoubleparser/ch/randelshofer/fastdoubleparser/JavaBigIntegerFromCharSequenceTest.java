/*
 * @(#)JavaBigIntegerFromCharSequenceTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.BigIntegerTestDataFactory.createLongRunningTestData;
import static ch.randelshofer.fastdoubleparser.BigIntegerTestDataFactory.createTestData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigIntegerFromCharSequenceTest {

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_CharSequence() {
        return createTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.charLength() == t.input().length()
                            && t.charOffset() == 0
                            && t.radix() == 10;
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_CharSequence_int() {
        return createTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.charLength() == t.input().length()
                            && t.charOffset() == 0;
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(),
                                u.radix()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_CharSequence_int_int() {
        return createTestData().stream()
                .filter(s -> {
                    NumberTestData t = s.supplier().get();
                    return t.radix() == 10;
                })
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(), u.charOffset(), u.charLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTests_parseBigInteger_CharSequence_int_int_int() {
        return createTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(), u.charOffset(), u.charLength(), u.radix()))));

    }

    @TestFactory
    @Disabled("long running test")
    public Stream<DynamicTest> dynamicTests_parseBigInteger_CharSequence_int_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parseBigInteger(u.input(), u.charOffset(), u.charLength(), u.radix()))));

    }

    private void test(NumberTestDataSupplier s, Function<NumberTestData, BigInteger> f) {
        NumberTestData d = s.supplier().get();
        BigInteger expectedValue = (BigInteger) d.expectedValue();
        BigInteger actual = null;
        try {
            actual = f.apply(d);
        } catch (NumberFormatException e) {
            if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                e.printStackTrace();
                // assertEquals(d.expectedErrorMessage(), e.getMessage());
            }
            assertEquals(d.expectedThrowableClass(), e.getClass());
        }
        if (expectedValue != null) {
            assertEquals(0, expectedValue.compareTo(actual),
                    "expected:" + expectedValue + " <> actual:" + actual);
            assertEquals(expectedValue, actual);
        } else {
            assertNull(actual);
        }
    }
}
