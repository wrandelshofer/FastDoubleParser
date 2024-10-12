/*
 * @(#)JavaBigDecimalFromByteArrayTest.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static ch.randelshofer.fastdoubleparser.BigDecimalTestDataFactory.createLongRunningTestData;
import static ch.randelshofer.fastdoubleparser.BigDecimalTestDataFactory.createRegularTestData;
import static ch.randelshofer.fastdoubleparser.VirtualCharSequence.toByteArray;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromByteArrayTest {


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
    @Disabled("long running test")
    public Stream<DynamicTest> dynamicTests_parseBigDecimal_byteArray_int_int_longRunningTests() {
        return createLongRunningTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimal(
                                toByteArray(u.input()),
                                u.byteOffset(), u.byteLength()))));

    }

    private void test(NumberTestDataSupplier s, Function<NumberTestData, BigDecimal> f) {
        NumberTestData d = s.supplier().get();
        BigDecimal expectedValue = (BigDecimal) d.expectedValue();
        BigDecimal actual = null;
        try {
            actual = f.apply(d);
        } catch (IllegalArgumentException e) {
            if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
                e.printStackTrace();
                assertEquals(d.expectedErrorMessage(), e.getMessage());
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
