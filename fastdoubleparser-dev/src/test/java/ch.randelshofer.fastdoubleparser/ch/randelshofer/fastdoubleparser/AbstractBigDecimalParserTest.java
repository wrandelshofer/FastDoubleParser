/*
 * @(#)AbstractBigDecimalParserTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class AbstractBigDecimalParserTest {
    private final boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));


    protected List<NumberTestDataSupplier> createDataForIllegalStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("NaN"),
                new NumberTestDataSupplier("+NaN"),
                new NumberTestDataSupplier("-NaN"),
                new NumberTestDataSupplier("NaNf"),
                new NumberTestDataSupplier("+NaNd"),
                new NumberTestDataSupplier("-NaNF"),
                new NumberTestDataSupplier("+-NaND"),
                new NumberTestDataSupplier("NaNInfinity"),
                new NumberTestDataSupplier("nan"),
                new NumberTestDataSupplier("Infinity"),
                new NumberTestDataSupplier("+Infinity"),
                new NumberTestDataSupplier("-Infinity"),
                new NumberTestDataSupplier("Infinit"),
                new NumberTestDataSupplier("+Infinityf"),
                new NumberTestDataSupplier("-InfinityF"),
                new NumberTestDataSupplier("+Infinityd"),
                new NumberTestDataSupplier("+-InfinityD"),
                new NumberTestDataSupplier("+InfinityNaN"),
                new NumberTestDataSupplier("infinity"),
                new NumberTestDataSupplier("empty", () -> new NumberTestData("", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("+", () -> new NumberTestData("+", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("-", () -> new NumberTestData("-", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("+e", () -> new NumberTestData("+e", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("-e", () -> new NumberTestData("-e", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("+e123", () -> new NumberTestData("-e123", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("-e456", () -> new NumberTestData("-e456", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("78 e9"),
                new NumberTestDataSupplier("-01 e23"),
                new NumberTestDataSupplier("- 1"),
                new NumberTestDataSupplier("-0 .5"),
                new NumberTestDataSupplier("-0. 5"),
                new NumberTestDataSupplier("-0.5 e"),
                new NumberTestDataSupplier("-0.5e 3"),
                new NumberTestDataSupplier("45\ne6"),
                new NumberTestDataSupplier("d"),
                new NumberTestDataSupplier(".f"),
                new NumberTestDataSupplier("7_8e90"),
                new NumberTestDataSupplier("12e3_4"),
                new NumberTestDataSupplier("00x5.6p7"),
                new NumberTestDataSupplier("89p0"),
                new NumberTestDataSupplier("cafebabe.1p2"),
                new NumberTestDataSupplier("0x123pa"),
                new NumberTestDataSupplier("0x1.2e7"),
                new NumberTestDataSupplier("0xp89"),

                new NumberTestDataSupplier("Exponent underflow", () -> new NumberTestData("1e" + (Integer.MIN_VALUE), AbstractNumberParser.VALUE_EXCEEDS_LIMITS, NumberFormatException.class)),
                new NumberTestDataSupplier("Exponent overflow", () -> new NumberTestData("1e" + (Integer.MAX_VALUE + 1L), AbstractNumberParser.VALUE_EXCEEDS_LIMITS, NumberFormatException.class)),

                new NumberTestDataSupplier("FloatTypeSuffix 1d", () -> new NumberTestData("1d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2d", () -> new NumberTestData("1.2d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2e-3d", () -> new NumberTestData("1.2e-3d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2E-3d", () -> new NumberTestData("1.2E-3d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2e-3d", () -> new NumberTestData("1.2e-3d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),

                new NumberTestDataSupplier(" 1.2e3"),
                new NumberTestDataSupplier("1.2e3 "),
                new NumberTestDataSupplier("  1.2e3"),
                new NumberTestDataSupplier("  -1.2e3"),
                new NumberTestDataSupplier("1.2e3  "),
                new NumberTestDataSupplier("   1.2e3   "),

                new NumberTestDataSupplier("FloatTypeSuffix 1D", () -> new NumberTestData("1D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2D", () -> new NumberTestData("1.2D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2e-3D", () -> new NumberTestData("1.2e-3D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2E-3D", () -> new NumberTestData("1.2E-3D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix -1.2e-3D", () -> new NumberTestData("-1.2e-3D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1f", () -> new NumberTestData("1f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2f", () -> new NumberTestData("1.2f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2e-3f", () -> new NumberTestData("1.2e-3f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2E-3f", () -> new NumberTestData("1.2E-3f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix -1.2e-3f", () -> new NumberTestData("-1.2e-3f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1F", () -> new NumberTestData("1F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2F", () -> new NumberTestData("1.2F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2e-3F", () -> new NumberTestData("1.2e-3F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix 1.2E-3F", () -> new NumberTestData("1.2E-3F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("FloatTypeSuffix -1.2e-3F", () -> new NumberTestData("-1.2e-3F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class))
        );
    }

    protected List<NumberTestDataSupplier> createDataForLegalDecStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("0", () -> new NumberTestData("0", new BigDecimal("0"))),
                new NumberTestDataSupplier("00", () -> new NumberTestData("00", new BigDecimal("0"))),
                new NumberTestDataSupplier("007", () -> new NumberTestData("007", new BigDecimal("7"))),
                new NumberTestDataSupplier("1", () -> new NumberTestData("1", new BigDecimal("1"))),
                new NumberTestDataSupplier("1.2", () -> new NumberTestData("1.2", new BigDecimal("1.2"))),
                new NumberTestDataSupplier("12.3", () -> new NumberTestData("12.3", new BigDecimal("12.3"))),
                new NumberTestDataSupplier("1.2e3", () -> new NumberTestData("1.2e3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("1.2E3", () -> new NumberTestData("1.2E3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("+1", () -> new NumberTestData("+1", new BigDecimal("1"))),
                new NumberTestDataSupplier("+1.2", () -> new NumberTestData("+1.2", new BigDecimal("1.2"))),
                new NumberTestDataSupplier("+1.2e3", () -> new NumberTestData("+1.2e3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("+1.2E3", () -> new NumberTestData("+1.2E3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("-1", () -> new NumberTestData("-1", new BigDecimal("-1"))),
                new NumberTestDataSupplier("-1.2", () -> new NumberTestData("-1.2", new BigDecimal("-1.2"))),
                new NumberTestDataSupplier("-1.2e3", () -> new NumberTestData("-1.2e3", new BigDecimal("-1.2e3"))),
                new NumberTestDataSupplier("-1.2E3", () -> new NumberTestData("-1.2E3", new BigDecimal("-1.2e3"))),
                new NumberTestDataSupplier("1.2e-3", () -> new NumberTestData("1.2e-3", new BigDecimal("1.2e-3"))),
                new NumberTestDataSupplier("1.2E-3", () -> new NumberTestData("1.2E-3", new BigDecimal("1.2e-3"))),

                new NumberTestDataSupplier("1.2E+3", () -> new NumberTestData("1.2E+3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("1.2e+3", () -> new NumberTestData("1.2e+3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("-1.2e+3", () -> new NumberTestData("-1.2e+3", new BigDecimal("-1.2e3"))),
                new NumberTestDataSupplier("-1.2E-3", () -> new NumberTestData("-1.2E-3", new BigDecimal("-1.2e-3"))),
                new NumberTestDataSupplier("+1.2E+3", () -> new NumberTestData("+1.2E+3", new BigDecimal("1.2e3"))),
                new NumberTestDataSupplier("1234567890", () -> new NumberTestData("1234567890", new BigDecimal("1234567890"))),
                new NumberTestDataSupplier("000000000", () -> new NumberTestData("000000000", new BigDecimal("000000000"))),
                new NumberTestDataSupplier("0000.0000", () -> new NumberTestData("0000.0000", new BigDecimal("0000.0000"))),

                new NumberTestDataSupplier("min exponent", () -> new NumberTestData("1e" + (Integer.MIN_VALUE + 1), BigDecimal.ONE.scaleByPowerOfTen(Integer.MIN_VALUE + 1))),
                new NumberTestDataSupplier("max exponent", () -> new NumberTestData("1e" + Integer.MAX_VALUE, BigDecimal.ONE.scaleByPowerOfTen(Integer.MAX_VALUE))),

                new NumberTestDataSupplier("8.99...99e68", () -> new NumberTestData("8." + (repeat("9", 19)) + "e68", new BigDecimal("8." + (repeat("9", 19)) + "e68"))),
                new NumberTestDataSupplier("103203303403503603703803903.122232425262728292", () -> new NumberTestData("103203303403503603703803903.122232425262728292", new BigDecimal("103203303403503603703803903.122232425262728292"))),
                new NumberTestDataSupplier("122232425262728292.103203303403503603703803903", () -> new NumberTestData("122232425262728292.103203303403503603703803903", new BigDecimal("122232425262728292.103203303403503603703803903"))),
                new NumberTestDataSupplier("-103203303403503603703803903.122232425262728292e6789", () -> new NumberTestData("-103203303403503603703803903.122232425262728292e6789", new BigDecimal("-103203303403503603703803903.122232425262728292e6789"))),
                new NumberTestDataSupplier("122232425262728292.103203303403503603703803903e-6789", () -> new NumberTestData("122232425262728292.103203303403503603703803903e-6789", new BigDecimal("122232425262728292.103203303403503603703803903e-6789"))),
                new NumberTestDataSupplier("-122232425262728292.103203303403503603703803903e-6789", () -> new NumberTestData("-122232425262728292.103203303403503603703803903e-6789", new BigDecimal("-122232425262728292.103203303403503603703803903e-6789")))
        );
    }

    protected List<NumberTestDataSupplier> createDataForLegalCroppedStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("x1y", () -> new NumberTestData("x1y", BigDecimal.ONE, 1, 1)),
                new NumberTestDataSupplier("xx+123e-456yyy", () -> new NumberTestData("xx+123e-456yyy", new BigDecimal("+123e-456"), 2, 9))
        );
    }

    protected List<NumberTestDataSupplier> createDataForIllegalCroppedStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("xx-0x1p2yyy", () -> new NumberTestData("xx-0x1p2yyy", null, 2, 6, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class))
        );
    }

    protected List<NumberTestDataSupplier> createDataForBigDecimalLimits() {
        return Arrays.asList(
                new NumberTestDataSupplier("BigDecimal Min Scale", () -> new NumberTestData("BigDecimal Min Scale",
                        BIG_DECIMAL_MIN_SCALE.toString(), BIG_DECIMAL_MIN_SCALE)),
                new NumberTestDataSupplier("BigDecimal Max Scale", () -> new NumberTestData("BigDecimal Max Scale",
                        BIG_DECIMAL_MAX_SCALE.toString(), BIG_DECIMAL_MAX_SCALE))

        );
    }

    /**
     * White-box tests for the following methods:
     * <ul>
     *     <li>{@link JavaBigDecimalFromByteArray#parseBigDecimalString(byte[], int, int, boolean)}</li>
     *     <li>{@link JavaBigDecimalFromCharArray#parseBigDecimalString(char[], int, int, boolean)}</li>
     *     <li>{@link JavaBigDecimalFromCharSequence#parseBigDecimalString(CharSequence, int, int, boolean)}</li>
     * </ul>
     */
    protected List<NumberTestDataSupplier> createTestDataForInputClassesInMethodParseBigDecimalString() {
        return Arrays.asList(
                new NumberTestDataSupplier("many digits threshold", () -> new NumberTestData("many digits threshold", repeat(" ", 32), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("not many digits threshold", () -> new NumberTestData("not many digits threshold", repeat(" ", 31), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal empty string", () -> new NumberTestData("", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal character", () -> new NumberTestData("§", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal only negative sign", () -> new NumberTestData("-", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal only positive sign", () -> new NumberTestData("+", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal only point", () -> new NumberTestData(".", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("integer significand", () -> new NumberTestData("1", BigDecimal.ONE)),
                new NumberTestDataSupplier("fractional significand", () -> new NumberTestData("0.1", BigDecimal::new)),
                new NumberTestDataSupplier("point before significand", () -> new NumberTestData(".1", BigDecimal::new)),
                new NumberTestDataSupplier("point after significand", () -> new NumberTestData("1.", BigDecimal::new)),
                new NumberTestDataSupplier("point before significand, 40 digits", () -> new NumberTestData(".1234567890123456789012345678901234567890", BigDecimal::new)),
                new NumberTestDataSupplier("point after significand, 40 digits", () -> new NumberTestData("1234567890123456789012345678901234567890.", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 18 digits in integer part", () -> new NumberTestData("123456789012345678", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 18 digits in fraction part", () -> new NumberTestData(".123456789012345678", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 18 digits in integer and fraction part together", () -> new NumberTestData("1234567890.12345678", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 19 digits in integer part", () -> new NumberTestData("1234567890123456789", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 19 digits in fraction part", () -> new NumberTestData(".1234567890123456789", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 19 digits in integer and fraction part together", () -> new NumberTestData("1234567890.123456789", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 40 digits in integer part", () -> new NumberTestData("1234567890123456789012345678901234567890", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 40 digits in fraction part", () -> new NumberTestData(".1234567890123456789012345678901234567890", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 40 digits in integer and fraction part together", () -> new NumberTestData("1234567890.123456789012345678901234567890", BigDecimal::new)),
                new NumberTestDataSupplier("illegal digit in significand with 18 digits in integer part", () -> new NumberTestData("123456789012345u78", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 18 digits in fraction part", () -> new NumberTestData(".1234567890123u5678", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 18 digits in integer and fraction part together", () -> new NumberTestData("123456789u.12345678", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 19 digits in integer part", () -> new NumberTestData("12345678901234567u9", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 19 digits in fraction part", () -> new NumberTestData(".12345678901234567u9", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 19 digits in integer and fraction part together", () -> new NumberTestData("1234567890.12345u789", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 40 digits in integer part", () -> new NumberTestData("1234567890123456789012345678901234567u9", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 40 digits in fraction part", () -> new NumberTestData(".1234567890123456789012345678901234567u9", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal digit in significand with 40 digits in integer and fraction part together", () -> new NumberTestData("123456789012345678901234567890.12345u789", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal only exponent indicator e", () -> new NumberTestData("e", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal only exponent indicator E", () -> new NumberTestData("E", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal exponent without number", () -> new NumberTestData("1e", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal exponent without number +", () -> new NumberTestData("1e+", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal exponent without number -", () -> new NumberTestData("1e-", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal exponent without number §", () -> new NumberTestData("1e§", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal duplicate point", () -> new NumberTestData("1.2.3e4", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal duplicate sign", () -> new NumberTestData("--1.2e4", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal duplicate sign after point", () -> new NumberTestData("-1.-2e5", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal duplicate sign inside significand", () -> new NumberTestData("-1-2e5", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal duplicate sign inside exponent", () -> new NumberTestData("-12e5-6", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal duplicate  exponent", () -> new NumberTestData("-12e5e6", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("significand with 18 digits in integer part and exponent", () -> new NumberTestData("123456789012345678e-887799", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 18 digits in fraction part and exponent", () -> new NumberTestData("-.123456789012345678e887799", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 18 digits in integer and fraction part together and exponent", () -> new NumberTestData("1234567890.12345678e-887799", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 19 digits in integer part and exponent", () -> new NumberTestData("-1234567890123456789e887799", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 19 digits in fraction part and exponent", () -> new NumberTestData("-.1234567890123456789e-887799", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 19 digits in integer and fraction part together and exponent", () -> new NumberTestData("-1234567890.123456789e887799", BigDecimal::new))
        );
    }

    /**
     * White-box tests for the following methods:
     * <ul>
     *     <li>{@link JavaBigDecimalFromByteArray#parseBigDecimalStringWithManyDigits(byte[], int, int, int)}</li>
     *     <li>{@link JavaBigDecimalFromCharArray#parseBigDecimalStringWithManyDigits(char[], int, int, int)}</li>
     *     <li>{@link JavaBigDecimalFromCharSequence#parseBigDecimalStringWithManyDigits(CharSequence, int, int, int)}</li>
     * </ul>
     */
    protected List<NumberTestDataSupplier> createTestDataForInputClassesInMethodParseBigDecimalStringWithManyDigits() {
        return Arrays.asList(
                new NumberTestDataSupplier("illegal only negative sign", () -> new NumberTestData("-" + repeat("\000", 32), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("illegal only positive sign", () -> new NumberTestData("+" + repeat("\000", 32), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),


                new NumberTestDataSupplier("significand with 40 zeroes in integer part", () -> new NumberTestData("significand with 40 zeroes in integer part", repeat("0", 40), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 40 zeroes in fraction part", () -> new NumberTestData("." + repeat("0", 40), BigDecimal::new)),

                new NumberTestDataSupplier("significand with 10 leading zeros and 30 digits in integer part", () -> new NumberTestData("significand with 10 leading zeros and 30 digits in integer part", repeat("0", 10) + repeat("9", 30), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 10 leading zeros and 30 digits in fraction part", () -> new NumberTestData("." + repeat("0", 10) + repeat("9", 30), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 10 leading zeros and 30 digits in integer part and in fraction part", () -> new NumberTestData("significand with 10 leading zeros and 30 digits in integer part and in fraction part", repeat("0", 10) + repeat("9", 30) + "." + repeat("0", 10) + repeat("9", 30), BigDecimal::new)),

                new NumberTestDataSupplier("significand with 40 digits in integer part and exponent", () -> new NumberTestData("-1234567890123456789012345678901234567890e887799", BigDecimal::new)),
                new NumberTestDataSupplier("no significand but exponent 40 digits", () -> new NumberTestData("-e12345678901234567890123456789012345678901234567890", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("significand with 40 digits in fraction part and exponent", () -> new NumberTestData("-.1234567890123456789012345678901234567890e-887799", BigDecimal::new)),
                new NumberTestDataSupplier("significand with 40 digits in integer and fraction part together and exponent", () -> new NumberTestData("-123456789012345678901234567890.1234567890e887799", BigDecimal::new)),

                new NumberTestDataSupplier("significand with 127 integer digits (below recursion threshold)", () -> new NumberTestData("significand with 127 integer digits (below recursion threshold)", new VirtualCharSequence('7', 127), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 128 integer digits (above recursion threshold)", () -> new NumberTestData("significand with 128 integer digits (above recursion threshold)", new VirtualCharSequence('7', 128), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 127 fraction digits (below recursion threshold)", () -> new NumberTestData("significand with 127 fraction digits (below recursion threshold)", new VirtualCharSequence(".", '7', 128), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 128 fraction digits (above recursion threshold)", () -> new NumberTestData("significand with 128 fraction digits (above recursion threshold)", new VirtualCharSequence(".", '7', 129), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 1023 integer digits (below parallel threshold)", () -> new NumberTestData("significand with 1023 integer digits (below parallel threshold)", new VirtualCharSequence('7', 1023), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 1024 integer digits (above parallel threshold)", () -> new NumberTestData("significand with 1024 integer digits (above parallel threshold)", new VirtualCharSequence('7', 1024), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 2048 integer digits (twice above parallel threshold)", () -> new NumberTestData("significand with 2048 integer digits (twice above parallel threshold)", new VirtualCharSequence('7', 2048), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 1023 fraction digits (below parallel threshold)", () -> new NumberTestData("significand with 1023 fraction digits (below parallel threshold)", new VirtualCharSequence(".", '7', 1024), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 1024 fraction digits (above parallel threshold)", () -> new NumberTestData("significand with 1024 fraction digits (above parallel threshold)", new VirtualCharSequence(".", '7', 1025), BigDecimal::new)),
                new NumberTestDataSupplier("significand with 2048 integer digits, 4096 fraction digits (above parallel threshold)", () -> new NumberTestData("significand with 2048 integer digits, 4096 fraction digits (above parallel threshold)", new VirtualCharSequence("", 2048, ".", "", '7', 2048 + 4096 + 1), BigDecimal::new))
        );
    }

    protected List<NumberTestDataSupplier> createDataWithVeryLongInputStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("significand too many input characters", () -> new NumberTestData("significand too many input characters", new VirtualCharSequence('1', Integer.MAX_VALUE - 3), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("significand too many non-zero digits", () -> new NumberTestData("significand too many non-zero digits", new VirtualCharSequence('1', 1_292_782_621 + 1), AbstractNumberParser.VALUE_EXCEEDS_LIMITS, NumberFormatException.class)),
                new NumberTestDataSupplier("significand with maximal number of zero digits in integer part", () -> new NumberTestData("significand with maximal number of zero digits in integer part", new VirtualCharSequence('0', 1_292_782_621), BigDecimal.ZERO)),
                new NumberTestDataSupplier("significand with maximal number of zero digits in fraction part", () -> new NumberTestData("significand with maximal number of zero digits in fraction part", new VirtualCharSequence(".", '0', 1_292_782_621 + 1), new BigDecimal("0E-1292782621"))),
                new NumberTestDataSupplier("significand with maximal number of zero digits in significand", () -> new NumberTestData("significand with maximal number of zero digits in significand", new VirtualCharSequence("", 1000, ".", "", '0', 1_292_782_621 + 1), new BigDecimal("0E-1292781621"))),

                new NumberTestDataSupplier("'7' ** (MAX_INPUT_LENGTH + 1)", () -> new NumberTestData("'7' ** (MAX_INPUT_LENGTH + 1)", new VirtualCharSequence('7', 1_292_782_635 + 1), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("'8' ** 646_456_993", () -> new NumberTestData("'8' ** 646_456_993", new VirtualCharSequence('8', 646_456_993), AbstractNumberParser.VALUE_EXCEEDS_LIMITS, NumberFormatException.class)),
                new NumberTestDataSupplier("' ' ** (MAX_INPUT_LENGTH + 1)", () -> new NumberTestData("' ' ** (MAX_INPUT_LENGTH + 1)", new VirtualCharSequence(' ', 1_292_782_635 + 1), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class))
        );
    }


    private final static BigDecimal BIG_DECIMAL_MIN_SCALE = new BigDecimal(BigInteger.ONE, Integer.MIN_VALUE + 1);
    private final static BigDecimal BIG_DECIMAL_MAX_SCALE = new BigDecimal(BigInteger.ONE, Integer.MAX_VALUE);

    List<NumberTestDataSupplier> createRegularTestData() {
        List<NumberTestDataSupplier> list = new ArrayList<>();
        list.addAll(createDataForBigDecimalLimits());
        list.addAll(createDataForIllegalStrings());
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createDataForIllegalCroppedStrings());
        list.addAll(createTestDataForInputClassesInMethodParseBigDecimalString());
        list.addAll(createTestDataForInputClassesInMethodParseBigDecimalStringWithManyDigits());
        if (longRunningTests) {
            list.addAll(createDataWithVeryLongInputStrings());
        }
        return list;
    }


    protected void test(NumberTestDataSupplier s, Function<NumberTestData, BigDecimal> f) {
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
