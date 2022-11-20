/*
 * @(#)JavaBigIntegerFromByteArrayTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigIntegerFromByteArrayTest extends AbstractBigIntegerParserTest {


    private void test(NumberTestData d, Function<NumberTestData, BigInteger> f) {
        BigInteger expectedValue = (BigInteger) d.expectedValue();
        BigInteger actual = f.apply(d);
        if (expectedValue != null) {
            assertEquals(0, actual == null ? -1 : expectedValue.compareTo(actual),
                    "expected:" + expectedValue.bitLength() + " <> actual:" + actual.bitLength());
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
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsVeryLongStrings() {
        return createDataForVeryLongDecStrings().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigIntegerParser.parallelParseBigInteger(u.input().toString().getBytes(StandardCharsets.ISO_8859_1),
                                u.byteOffset(), u.byteLength()))));

    }
}
