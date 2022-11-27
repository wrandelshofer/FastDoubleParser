/*
 * @(#)JavaBigDecimalFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromByteArrayTest extends AbstractBigDecimalParserTest {


    private void test(NumberTestData d, Function<NumberTestData, BigDecimal> f) {
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

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParseBigDecimalFromByteArray() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parallelParseBigDecimal(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicInputClassesTest() {
        return Stream.concat(
                        createTestDataForInputClassesInMethodParseBigDecimalString().stream(),
                        createTestDataForInputClassesInMethodParseBigDecimalStringWithManyDigits().stream()
                )
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parallelParseBigDecimal(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }
}
