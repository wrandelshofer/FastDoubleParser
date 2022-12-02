/*
 * @(#)JavaBigIntegerFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigIntegerFromCharSequenceTest extends AbstractBigIntegerParserTest {


    private void test(NumberTestData d, Function<NumberTestData, BigInteger> f) {
        BigInteger expectedValue = (BigInteger) d.expectedValue();
        BigInteger actual = null;
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
            assertEquals(0, actual == null ? -1 : expectedValue.compareTo(actual),
                    "expected:" + expectedValue.bitLength() + " <> actual:" + actual.bitLength());
            assertEquals(expectedValue, actual);
        } else {
            assertNull(actual);
        }
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParseBigIntegerFromCharSequence() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input(),
                                u.charOffset(), u.charLength()))));

    }

    @TestFactory
    @Disabled
    public Stream<DynamicTest> dynamicTestsVeryLongStrings() {
        return createDataForVeryLongDecStrings().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input(),
                                u.charOffset(), u.charLength()))));

    }
}
