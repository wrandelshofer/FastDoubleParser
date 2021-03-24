/*
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package org.fastdoubleparser.parser;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class FastDoubleParserTest {
    @TestFactory
    List<DynamicNode> dynamicTestsIllegalInputs() {
        return List.of(
                dynamicTest("empty", () -> testIllegalInput("")),
                dynamicTest("-", () -> testIllegalInput("-")),
                dynamicTest("+", () -> testIllegalInput("+")),
                dynamicTest("1e", () -> testIllegalInput("1e"))
        );
    }

    @TestFactory
    List<DynamicNode> dynamicTestsLegalInputs() {
        return List.of(
                dynamicTest("1e23", () -> testLegalInput("1e23", 1e23)),
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
                dynamicTest("3.7587182468424695418288325e-309", () -> testLegalInput("3.7587182468424695418288325e-309", 3.7587182468424695418288325e-309)),
                dynamicTest("9007199254740992.e-256", () -> testLegalInput("9007199254740992.e-256", 9007199254740992.e-256)),
                dynamicTest("0x1.921fb54442d18p1", () -> testLegalInput("0x1.921fb54442d18p1", 0x1.921fb54442d18p1)),
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
                dynamicTest(Double.MIN_VALUE + "", () -> testLegalInput(
                        Double.MIN_VALUE)),
                dynamicTest(Double.MAX_VALUE + "", () -> testLegalInput(
                        Double.MAX_VALUE)),
                dynamicTest(Double.POSITIVE_INFINITY + "", () -> testLegalInput(
                        Double.MIN_VALUE)),
                dynamicTest(Double.NEGATIVE_INFINITY + "", () -> testLegalInput(
                        Double.NEGATIVE_INFINITY)),
                dynamicTest(Double.NaN + "", () -> testLegalInput(
                        Double.NaN)),
                dynamicTest(7.2057594037927933e+16 + "", () -> testLegalInput(
                        7.2057594037927933e+16)),
                dynamicTest(-7.2057594037927933e+16 + "", () -> testLegalInput(
                        -7.2057594037927933e+16)),
                dynamicTest(-4.8894481170331026E-173 + "", () -> testLegalInput(
                        -4.8894481170331026E-173)),
                dynamicTest(4.8894481170331026E-173 + "", () -> testLegalInput(
                        4.8894481170331026E-173)),
                dynamicTest(-4.889448117033103E-173 + "", () -> testLegalInput(
                        -4.889448117033103E-173)),
                dynamicTest(4.889448117033103E-173 + "", () -> testLegalInput(
                        4.889448117033103E-173)),
                dynamicTest(2.348957380189919E-199 + "", () -> testLegalInput(
                        2.348957380189919E-199)),
                dynamicTest(-2.348957380189919E-199 + "", () -> testLegalInput(
                        -2.348957380189919E-199)),
                dynamicTest(-6.658066127037204E87 + "", () -> testLegalInput(
                        -6.658066127037204E87)),
                dynamicTest(6.658066127037204E87 + "", () -> testLegalInput(
                        6.658066127037204E87)),
                dynamicTest(4.559067278662733E288 + "", () -> testLegalInput(
                        4.559067278662733E288)),
                dynamicTest(-4.559067278662733E288 + "", () -> testLegalInput(
                        -4.559067278662733E288))
        );
    }

    @TestFactory
    Stream<DynamicNode> dynamicTestsPowerOfTen() {
        return IntStream.range(-307, 309).mapToObj(i -> "1e" + i)
                .map(d -> dynamicTest(d, () -> testLegalInput(d, Double.parseDouble(d))));
    }

    @TestFactory
    Stream<DynamicNode> dynamicTestsRandomInputs() {
        Random r = new Random(0);
        return r.longs(20_000)
                .mapToDouble(Double::longBitsToDouble)
                .mapToObj(d -> dynamicTest(d + "", () -> testLegalInput(d)));
    }

    @TestFactory
    Stream<DynamicNode> testErrorCases() throws IOException {
        return Files.lines(Path.of("data/FastDoubleParser_errorcases.txt"))
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
    }

    private void testLegalInput(double expected) {
        testLegalInput(expected + "", expected);
    }

    private void testLegalInput(String str, double expected) {
        double actual = FastDoubleParser.parseDouble(str);
        assertEquals(expected, actual, "str=" + str);
        assertEquals(Double.doubleToLongBits(expected), Double.doubleToLongBits(actual),
                "longBits of " + expected);
    }
}