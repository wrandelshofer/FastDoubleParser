/*
 * @(#)ParseFloatWithVectorTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class LongFromByteArrayTest {

    private void doIllegalTest(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
        try {
            Long.parseLong(s);
            fail();
        } catch (NumberFormatException e) {
            //success;
        }
        try {
            new LongFromCharSequence().parseLong(s, 0, s.length());
            fail();
        } catch (NumberFormatException e) {
            //success;
        }
        try {
            new LongFromByteArray().parseLong(bytes, 0, bytes.length);
            fail();
        } catch (NumberFormatException e) {
            //success;
        }
        try {
            new LongFromByteArray().parseLongVectorized(bytes, 0, bytes.length);
            fail();
        } catch (NumberFormatException e) {
            //success;
        }
    }

    private void doLegalTest(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
        long actualCharSequence = new LongFromCharSequence().parseLong(s, 0, s.length());
        long actualScalar = new LongFromByteArray().parseLong(bytes, 0, bytes.length);
        long actualVector = new LongFromByteArray().parseLongVectorized(bytes, 0, bytes.length);
        long expected = Long.parseLong(s);
        assertEquals(expected, actualCharSequence);
        assertEquals(expected, actualScalar);
        assertEquals(expected, actualVector);
    }

    private void doLegalTestMoreThan16Chars(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.ISO_8859_1);
        long actualScalar = new LongFromByteArray().parseLong(bytes, 0, bytes.length);
        long actualCharSequence = new LongFromCharSequence().parseLong(s, 0, s.length());
        long expected = Long.parseLong(s);
        assertEquals(expected, actualScalar);
        assertEquals(expected, actualCharSequence);
    }

    @TestFactory
    List<DynamicNode> dynamicTestsIllegalInput() {
        return List.of(
                dynamicTest("<empt>", () -> doIllegalTest("")),
                dynamicTest("<blank>", () -> doIllegalTest(" ")),
                dynamicTest(" 1", () -> doIllegalTest(" 1")),
                dynamicTest("1 ", () -> doIllegalTest("1 ")),
                dynamicTest("+", () -> doIllegalTest("+")),
                dynamicTest("-", () -> doIllegalTest("-")),
                dynamicTest("+-68", () -> doIllegalTest("+-68")),
                dynamicTest("68-", () -> doIllegalTest("68-")),
                dynamicTest("-+68", () -> doIllegalTest("-+68")),
                dynamicTest("--68", () -> doIllegalTest("++68")),
                dynamicTest("123456789012345x", () -> doIllegalTest("123456789012345x")),
                dynamicTest("123456789x123456", () -> doIllegalTest("123456789x123456")),
                dynamicTest("x23456789x123456", () -> doIllegalTest("x23456789x123456")),
                dynamicTest("123456789012345 ", () -> doIllegalTest("123456789012345 ")),
                dynamicTest("123456789 123456", () -> doIllegalTest("123456789 123456")),
                dynamicTest(" 23456789 123456", () -> doIllegalTest(" 23456789 123456")),
                dynamicTest("-92233720368547758080", () -> doIllegalTest("-92233720368547758080")),
                dynamicTest("92233720368547758070", () -> doIllegalTest("92233720368547758070"))

        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalInput16Chars() {
        return List.of(
                dynamicTest("1", () -> doLegalTest("1")),
                dynamicTest("-567", () -> doLegalTest("-567")),
                dynamicTest("+321", () -> doLegalTest("+321")),
                dynamicTest("1234567890123456", () -> doLegalTest("1234567890123456")),
                dynamicTest("-123456789012345", () -> doLegalTest("-123456789012345")),
                dynamicTest("+123456789012345", () -> doLegalTest("+123456789012345"))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalInputMoreThan16Chars() {
        return List.of(
                dynamicTest("-123456789012345678", () -> doLegalTestMoreThan16Chars("-123456789012345678")),
                dynamicTest("123456789012345678", () -> doLegalTestMoreThan16Chars("123456789012345678")),
                dynamicTest("-9223372036854775808", () -> doLegalTestMoreThan16Chars("-9223372036854775808")),
                dynamicTest("9223372036854775807", () -> doLegalTestMoreThan16Chars("9223372036854775807")),
                dynamicTest("-09223372036854775808", () -> doLegalTestMoreThan16Chars("-09223372036854775808")),
                dynamicTest("09223372036854775807", () -> doLegalTestMoreThan16Chars("09223372036854775807"))
        );
    }


}
