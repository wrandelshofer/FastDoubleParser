/*
 * @(#)HandPickedTest.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class HandPickedTest {
    private static class MaxLengthCharSequence implements CharSequence {
        private final String str;

        private MaxLengthCharSequence(String str) {
            this.str = str;
        }

        @Override
        public int length() {
            return Integer.MAX_VALUE;
        }

        @Override
        public char charAt(int index) {
            return index < Integer.MAX_VALUE - str.length()
                    ? ' '
                    : str.charAt(index - (Integer.MAX_VALUE - str.length()));
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return str;
        }
    }

    @TestFactory
    List<DynamicNode> dynamicTestsIllegalInputs() {
        return Arrays.asList(
                dynamicTest("empty", () -> testIllegalInput("")),
                dynamicTest("-", () -> testIllegalInput("-")),
                dynamicTest("+", () -> testIllegalInput("+")),
                dynamicTest("1e", () -> testIllegalInput("1e")),
                dynamicTest("1_000", () -> testIllegalInput("1_000")),
                dynamicTest("0.000_1", () -> testIllegalInput("0.000_1")),
                dynamicTest("-e-55", () -> testIllegalInput("-e-55")),
                dynamicTest("1 x", () -> testIllegalInput("1 x")),
                dynamicTest("x 1", () -> testIllegalInput("x 1")),
                dynamicTest("1§", () -> testIllegalInput("1§")),
                dynamicTest("NaN x", () -> testIllegalInput("NaN x")),
                dynamicTest("Infinity x", () -> testIllegalInput("Infinity x")),
                dynamicTest("0x123.456789abcde", () -> testIllegalInput("0x123.456789abcde"))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsIllegalMaxLengthInputs() {
        return Arrays.asList(
                dynamicTest("empty", () -> testIllegalMaxLengthInput("")),
                dynamicTest("-", () -> testIllegalMaxLengthInput("-")),
                dynamicTest("+", () -> testIllegalMaxLengthInput("+")),
                dynamicTest("1e", () -> testIllegalMaxLengthInput("1e")),
                dynamicTest("1_000", () -> testIllegalMaxLengthInput("1_000")),
                dynamicTest("0.000_1", () -> testIllegalMaxLengthInput("0.000_1")),
                dynamicTest("-e-55", () -> testIllegalMaxLengthInput("-e-55")),
                dynamicTest("1 x", () -> testIllegalMaxLengthInput("1 x")),
                dynamicTest("x 1", () -> testIllegalMaxLengthInput("x 1")),
                dynamicTest("1§", () -> testIllegalMaxLengthInput("1§")),
                dynamicTest("NaN x", () -> testIllegalMaxLengthInput("NaN x")),
                dynamicTest("Infinity x", () -> testIllegalMaxLengthInput("Infinity x")),
                dynamicTest("0x123.456789abcde", () -> testIllegalMaxLengthInput("0x123.456789abcde"))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalDecFloatLiterals() {
        return Arrays.asList(
                dynamicTest("1e23", () -> testLegalInput("1e23", 1e23)),
                dynamicTest("whitespace before 1", () -> testLegalInput(" 1")),
                dynamicTest("whitespace after 1", () -> testLegalInput("1 ")),
                dynamicTest("0", () -> testLegalInput("0", 0.0)),
                dynamicTest("-0", () -> testLegalInput("-0", -0.0)),
                dynamicTest("+0", () -> testLegalInput("+0", +0.0)),
                dynamicTest("-0.0", () -> testLegalInput("-0.0", -0.0)),
                dynamicTest("-0.0e-22", () -> testLegalInput("-0.0e-22", -0.0e-22)),
                dynamicTest("-0.0e24", () -> testLegalInput("-0.0e24", -0.0e24)),
                dynamicTest("0e555", () -> testLegalInput("0e555", 0.0)),
                dynamicTest("-0e555", () -> testLegalInput("-0e555", -0.0)),
                dynamicTest("1", () -> testLegalInput("1", 1.0)),
                dynamicTest("-1", () -> testLegalInput("-1", -1.0)),
                dynamicTest("+1", () -> testLegalInput("+1", +1.0)),
                dynamicTest("1e0", () -> testLegalInput("1e0", 1e0)),
                dynamicTest("1.e0", () -> testLegalInput("1.e0", 1e0)),
                dynamicTest(".e2", () -> testLegalInput(".e2", 0)),
                dynamicTest("1e1", () -> testLegalInput("1e1", 1e1)),
                dynamicTest("1e+1", () -> testLegalInput("1e+1", 1e+1)),
                dynamicTest("1e-1", () -> testLegalInput("1e-1", 1e-1)),
                dynamicTest("0049", () -> testLegalInput("0049", 49)),
                dynamicTest("9999999999999999999", () -> testLegalInput("9999999999999999999", 9999999999999999999d)),
                dynamicTest("972150611626518208.0", () -> testLegalInput("972150611626518208.0", 9.7215061162651827E17)),
                dynamicTest("3.7587182468424695418288325e-309", () -> testLegalInput("3.7587182468424695418288325e-309", 3.7587182468424695418288325e-309)),
                dynamicTest("9007199254740992.e-256", () -> testLegalInput("9007199254740992.e-256", 9007199254740992.e-256)),
                dynamicTest("0.1e+3", () -> testLegalInput("0.1e+3",
                        100.0)),
                dynamicTest("0.00000000000000000000000000000000000000000001e+46",
                        () -> testLegalInput("0.00000000000000000000000000000000000000000001e+46",
                                100.0)),
                dynamicTest("10000000000000000000000000000000000000000000e+308",
                        () -> testLegalInput("10000000000000000000000000000000000000000000e+308",
                                Double.parseDouble("10000000000000000000000000000000000000000000e+308"))),
                dynamicTest("3.1415926535897932384626433832795028841971693993751", () -> testLegalInput(
                        "3.1415926535897932384626433832795028841971693993751",
                        Double.parseDouble("3.1415926535897932384626433832795028841971693993751"))),
                dynamicTest("314159265358979323846.26433832795028841971693993751e-20", () -> testLegalInput(
                        "314159265358979323846.26433832795028841971693993751e-20",
                        3.141592653589793)),
                dynamicTest("1e-326", () -> testLegalInput(
                        "1e-326", 0.0)),
                dynamicTest("1e-325", () -> testLegalInput(
                        "1e-325", 0.0)),
                dynamicTest("1e310", () -> testLegalInput(
                        "1e310", Double.POSITIVE_INFINITY)),
                dynamicTest(7.2057594037927933e+16 + "", () -> testLegalDecInput(
                        7.2057594037927933e+16)),
                dynamicTest(-7.2057594037927933e+16 + "", () -> testLegalDecInput(
                        -7.2057594037927933e+16)),
                dynamicTest(-4.8894481170331026E-173 + "", () -> testLegalDecInput(
                        -4.8894481170331026E-173)),
                dynamicTest(4.8894481170331026E-173 + "", () -> testLegalDecInput(
                        4.8894481170331026E-173)),
                dynamicTest(-4.889448117033103E-173 + "", () -> testLegalDecInput(
                        -4.889448117033103E-173)),
                dynamicTest(4.889448117033103E-173 + "", () -> testLegalDecInput(
                        4.889448117033103E-173)),
                dynamicTest(2.348957380189919E-199 + "", () -> testLegalDecInput(
                        2.348957380189919E-199)),
                dynamicTest(-2.348957380189919E-199 + "", () -> testLegalDecInput(
                        -2.348957380189919E-199)),
                dynamicTest(-6.658066127037204E87 + "", () -> testLegalDecInput(
                        -6.658066127037204E87)),
                dynamicTest(6.658066127037204E87 + "", () -> testLegalDecInput(
                        6.658066127037204E87)),
                dynamicTest(4.559067278662733E288 + "", () -> testLegalDecInput(
                        4.559067278662733E288)),
                dynamicTest(-4.559067278662733E288 + "", () -> testLegalDecInput(
                        -4.559067278662733E288))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalDecFloatMaxLengthLiterals() {
        return Arrays.asList(
                dynamicTest("1e23", () -> testLegalMaxLengthInput("1e23", 1e23)),
                dynamicTest("whitespace after 1", () -> testLegalMaxLengthInput("1 ", 1)),
                dynamicTest("0", () -> testLegalMaxLengthInput("0", 0.0)),
                dynamicTest("-0", () -> testLegalMaxLengthInput("-0", -0.0)),
                dynamicTest("+0", () -> testLegalMaxLengthInput("+0", +0.0)),
                dynamicTest("-0.0", () -> testLegalMaxLengthInput("-0.0", -0.0)),
                dynamicTest("-0.0e-22", () -> testLegalMaxLengthInput("-0.0e-22", -0.0e-22)),
                dynamicTest("-0.0e24", () -> testLegalMaxLengthInput("-0.0e24", -0.0e24)),
                dynamicTest("0e555", () -> testLegalMaxLengthInput("0e555", 0.0)),
                dynamicTest("-0e555", () -> testLegalMaxLengthInput("-0e555", -0.0)),
                dynamicTest("1", () -> testLegalMaxLengthInput("1", 1.0)),
                dynamicTest("-1", () -> testLegalMaxLengthInput("-1", -1.0)),
                dynamicTest("+1", () -> testLegalMaxLengthInput("+1", +1.0)),
                dynamicTest("1e0", () -> testLegalMaxLengthInput("1e0", 1e0)),
                dynamicTest("1.e0", () -> testLegalMaxLengthInput("1.e0", 1e0)),
                dynamicTest(".e2", () -> testLegalMaxLengthInput(".e2", 0)),
                dynamicTest("1e1", () -> testLegalMaxLengthInput("1e1", 1e1)),
                dynamicTest("1e+1", () -> testLegalMaxLengthInput("1e+1", 1e+1)),
                dynamicTest("1e-1", () -> testLegalMaxLengthInput("1e-1", 1e-1)),
                dynamicTest("0049", () -> testLegalMaxLengthInput("0049", 49)),
                dynamicTest("9999999999999999999", () -> testLegalMaxLengthInput("9999999999999999999", 9999999999999999999d)),
                dynamicTest("972150611626518208.0", () -> testLegalMaxLengthInput("972150611626518208.0", 9.7215061162651827E17)),
                dynamicTest("3.7587182468424695418288325e-309", () -> testLegalMaxLengthInput("3.7587182468424695418288325e-309", 3.7587182468424695418288325e-309)),
                dynamicTest("9007199254740992.e-256", () -> testLegalMaxLengthInput("9007199254740992.e-256", 9007199254740992.e-256)),
                dynamicTest("0.1e+3", () -> testLegalMaxLengthInput("0.1e+3",
                        100.0))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalHexFloatLiterals() {
        return Arrays.asList(
                dynamicTest("0x1.0p8", () -> testLegalInput("0x1.0p8", 256))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalHexFloatMaxLengthLiterals() {
        return Arrays.asList(
                dynamicTest("0x1.0p8", () -> testLegalMaxLengthInput("0x1.0p8", 256))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalDecFloatLiteralsExtremeValues() {
        return Arrays.asList(
                dynamicTest(Double.toString(Double.MIN_VALUE), () -> testLegalDecInput(
                        Double.MIN_VALUE)),
                dynamicTest(Double.toString(Double.MAX_VALUE), () -> testLegalDecInput(
                        Double.MAX_VALUE)),
                dynamicTest(Double.toString(Double.POSITIVE_INFINITY), () -> testLegalDecInput(
                        Double.POSITIVE_INFINITY)),
                dynamicTest(Double.toString(Double.NEGATIVE_INFINITY), () -> testLegalDecInput(
                        Double.NEGATIVE_INFINITY)),
                dynamicTest(Double.toString(Double.NaN), () -> testLegalDecInput(
                        Double.NaN)),
                dynamicTest("Just above MAX_VALUE: 1.7976931348623159E308", () -> testLegalInput(
                        "1.7976931348623159E308", Double.POSITIVE_INFINITY)),
                dynamicTest("Just below MIN_VALUE: 2.47E-324", () -> testLegalInput(
                        "2.47E-324", 0.0))
        );
    }


    /**
     * Tests input classes that execute different code branches in
     * method {@link FastDoubleMath#tryDecToDoubleWithFastAlgorithm(boolean, long, int)}.
     */
    @TestFactory
    List<DynamicNode> dynamicTestsDecFloatLiteralClingerInputClasses() {
        return Arrays.asList(
                //
                dynamicTest("Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", () -> testLegalInput(
                        "9007199254740991e22")),
                dynamicTest("Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", () -> testLegalInput(
                        "9007199254740991e23")),
                dynamicTest("Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", () -> testLegalInput(
                        "9007199254740992e22")),
                dynamicTest("Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", () -> testLegalInput(
                        "1e-22")),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testLegalInput(
                        "1e-23"))
        );
    }

    /**
     * Tests input classes that execute different code branches in
     * method {@link FastDoubleMath#tryHexToDoubleWithFastAlgorithm(boolean, long, int)}.
     */
    @TestFactory
    List<DynamicNode> dynamicTestsHexFloatLiteralClingerInputClasses() {
        return Arrays.asList(
                dynamicTest("Inside Clinger fast path (max_clinger_significand)", () -> testLegalInput(
                        "0x1fffffffffffffp74", 0x1fffffffffffffp74)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", () -> testLegalInput(
                        "0x1fffffffffffffp74", 0x1fffffffffffffp74)),
                dynamicTest("Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", () -> testLegalInput(
                        "0x20000000000000p74", 0x20000000000000p74)),
                dynamicTest("Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", () -> testLegalInput(
                        "0x1p-74", 0x1p-74)),
                dynamicTest("Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", () -> testLegalInput(
                        "0x1p-75", 0x1p-75))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalHexFloatLiteralsExtremeValues() {
        return Arrays.asList(
                dynamicTest(Double.toHexString(Double.MIN_VALUE), () -> testLegalHexInput(
                        Double.MIN_VALUE)),
                dynamicTest(Double.toHexString(Double.MAX_VALUE), () -> testLegalHexInput(
                        Double.MAX_VALUE)),
                dynamicTest(Double.toHexString(Double.POSITIVE_INFINITY), () -> testLegalHexInput(
                        Double.POSITIVE_INFINITY)),
                dynamicTest(Double.toHexString(Double.NEGATIVE_INFINITY), () -> testLegalHexInput(
                        Double.NEGATIVE_INFINITY)),
                dynamicTest(Double.toHexString(Double.NaN), () -> testLegalHexInput(
                        Double.NaN)),
                dynamicTest("Just above MAX_VALUE: 0x1.fffffffffffff8p1023", () -> testLegalInput(
                        "0x1.fffffffffffff8p1023", Double.POSITIVE_INFINITY)),
                dynamicTest("Just below MIN_VALUE: 0x0.00000000000008p-1022", () -> testLegalInput(
                        "0x0.00000000000008p-1022", 0.0))
        );
    }

    @TestFactory
    Stream<DynamicNode> dynamicTestsPowerOfTen() {
        return IntStream.range(-307, 309).mapToObj(i -> "1e" + i)
                .map(d -> dynamicTest(d, () -> testLegalInput(d, Double.parseDouble(d))));
    }


    @TestFactory
    Stream<DynamicNode> testErrorCases() throws IOException {
        return Files.lines(FileSystems.getDefault().getPath("data/FastDoubleParser_errorcases.txt"))
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(str -> dynamicTest(str, () -> testLegalInput(str, Double.parseDouble(str))));
    }

    private void testIllegalInput(String s) {
        try {
            FastDoubleParser.parseDouble(s);
            fail();
        } catch (NumberFormatException e) {
            // success
        }
        try {
            FastDoubleParserFromByteArray.parseDouble(s.getBytes(StandardCharsets.ISO_8859_1));
            fail();
        } catch (NumberFormatException e) {
            // success
        }
    }

    private void testIllegalMaxLengthInput(String s) {
        try {
            FastDoubleParser.parseDouble(new MaxLengthCharSequence(s));
            fail();
        } catch (NumberFormatException e) {
            // success
        }
    }

    private void testLegalDecInput(double expected) {
        testLegalInput(expected + "", expected);
    }

    private void testLegalHexInput(double expected) {
        testLegalInput(Double.toHexString(expected), expected);
    }

    private void testLegalInput(String str) {
        testLegalInput(str, Double.parseDouble(str));
    }

    private void testLegalInput(String str, double expected) {
        double actual = FastDoubleParser.parseDouble(str);
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                "longBits of " + expected);

        double actualFromByteArray = FastDoubleParserFromByteArray.parseDouble(str.getBytes(StandardCharsets.ISO_8859_1));
        assertEquals(expected, actualFromByteArray, "str=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actualFromByteArray),
                "longBits of " + expected);
    }

    private void testLegalMaxLengthInput(String str, double expected) {
        double actual = FastDoubleParser.parseDouble(new MaxLengthCharSequence(str));
        assertEquals(expected, actual, "str(length=Integer.MAX_VALUE)=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                "longBits of " + expected);
    }
}