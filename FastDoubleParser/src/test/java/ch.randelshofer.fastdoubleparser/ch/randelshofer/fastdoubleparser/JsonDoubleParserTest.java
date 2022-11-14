/*
 * @(#)FastDoubleParserTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests class {@link JsonDoubleParser}
 */
public class JsonDoubleParserTest extends AbstractJsonFloatValueParserTest {
    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharSequence() {
        return createAllTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharSequenceIntInt() {
        return createAllTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleByteArray() {
        return createAllTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8)))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleByteArrayIntInt() {
        return createAllTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().getBytes(StandardCharsets.UTF_8), u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharArray() {
        return createAllTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().toCharArray()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleCharArrayIntInt() {
        return createAllTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JsonDoubleParser.parseDouble(u.input().toString().toCharArray(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleBitsCharSequenceIntInt() {
        return createAllTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> testBits(t, u -> JsonDoubleParser.parseDoubleBits(u.input(), u.charOffset(), u.charLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleBitsByteArrayIntInt() {
        return createAllTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> testBits(t, u -> JsonDoubleParser.parseDoubleBits(u.input().toString().getBytes(StandardCharsets.UTF_8), u.byteOffset(), u.byteLength()))));
    }

    @TestFactory
    public Stream<DynamicNode> dynamicTestsParseDoubleBitsCharArrayIntInt() {
        return createAllTestData().stream()
                .map(t -> dynamicTest(t.title(),
                        () -> testBits(t, u -> JsonDoubleParser.parseDoubleBits(u.input().toString().toCharArray(), u.charOffset(), u.charLength()))));
    }

    private void test(FloatTestData d, ToDoubleFunction<FloatTestData> f) {
        if (!d.valid()) {
            try {
                assertEquals(-1L, f.applyAsDouble(d));
            } catch (Exception e) {
                //success
            }
        } else {
            double actual = f.applyAsDouble(d);
            assertEquals(d.expectedDoubleValue(), actual);
        }
    }

    private void testBits(FloatTestData d, ToLongFunction<FloatTestData> f) {
        if (!d.valid()) {
            assertEquals(-1L, f.applyAsLong(d));
        } else {
            assertEquals(d.expectedDoubleValue(), Double.longBitsToDouble(f.applyAsLong(d)));
        }
    }

}
