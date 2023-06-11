/*
 * @(#)AbstractJavaFloatParserTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractJavaFloatParserTest extends AbstractFloatValueParserTest {
    protected final static int EXPECTED_MAX_INPUT_LENGTH = Integer.MAX_VALUE - 4;

    protected List<NumberTestData> createTestDataForNaN() {
        return Arrays.asList(
                new NumberTestData("NaN", Double.NaN),
                new NumberTestData("+NaN", Double.NaN),
                new NumberTestData("-NaN", Double.NaN),
                new NumberTestData("NaNf"),
                new NumberTestData("+NaNd"),
                new NumberTestData("-NaNF"),
                new NumberTestData("+-NaND"),
                new NumberTestData("NaNInfinity"),
                new NumberTestData("nan")
        );
    }

    protected List<NumberTestData> createTestDataForInfinity() {
        return Arrays.asList(
                new NumberTestData("Infinity", Double.POSITIVE_INFINITY),
                new NumberTestData("+Infinity", Double.POSITIVE_INFINITY),
                new NumberTestData("-Infinity", Double.NEGATIVE_INFINITY),
                new NumberTestData("Infinit"),
                new NumberTestData("+Infinityf"),
                new NumberTestData("-InfinityF"),
                new NumberTestData("+Infinityd"),
                new NumberTestData("+-InfinityD"),
                new NumberTestData("+InfinityNaN"),
                new NumberTestData("infinity")
        );
    }

    /**
     * ALl these strings must throw a {@link NumberFormatException}.
     */
    protected List<NumberTestData> createDataForBadStrings() {
        return Arrays.asList(
                new NumberTestData("empty", "", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("+"),
                new NumberTestData("-"),
                new NumberTestData("+e"),
                new NumberTestData("-e"),
                new NumberTestData("+e123"),
                new NumberTestData("-e456"),
                new NumberTestData("78 e9"),
                new NumberTestData("-01 e23"),
                new NumberTestData("- 1"),
                new NumberTestData("-0 .5"),
                new NumberTestData("-0. 5"),
                new NumberTestData("-0.5 e"),
                new NumberTestData("-0.5e 3"),
                new NumberTestData("45\ne6"),
                new NumberTestData("d"),
                new NumberTestData(".f"),
                new NumberTestData("7_8e90"),
                new NumberTestData("12e3_4"),
                new NumberTestData("00x5.6p7"),
                new NumberTestData("89p0"),
                new NumberTestData("cafebabe.1p2"),
                new NumberTestData("0x123pa"),
                new NumberTestData("0x1.2e7"),
                new NumberTestData("0xp89")
        );
    }

    protected List<NumberTestData> createDataForLegalDecStrings() {
        return Arrays.asList(
                new NumberTestData("0", 0f),
                new NumberTestData("00", 0f),
                new NumberTestData("007", 7f),
                new NumberTestData("1", 1f),
                new NumberTestData("1.2", 1.2f),
                new NumberTestData("1.2e3", 1.2e3f),
                new NumberTestData("1.2E3", 1.2e3f),
                new NumberTestData("1.2e3", 1.2e3f),
                new NumberTestData("+1", 1f),
                new NumberTestData("+1.2", 1.2f),
                new NumberTestData("+1.2e3", 1.2e3f),
                new NumberTestData("+1.2E3", 1.2e3f),
                new NumberTestData("+1.2e3", 1.2e3f),
                new NumberTestData("-1", -1f),
                new NumberTestData("-1.2", -1.2f),
                new NumberTestData("-1.2e3", -1.2e3f),
                new NumberTestData("-1.2E3", -1.2e3f),
                new NumberTestData("-1.2e3", -1.2e3f),
                new NumberTestData("1", 1f),
                new NumberTestData("1.2", 1.2f),
                new NumberTestData("1.2e-3", 1.2e-3f),
                new NumberTestData("1.2E-3", 1.2e-3f),
                new NumberTestData("1.2e-3", 1.2e-3f),

                new NumberTestData("FloatTypeSuffix", "1d", 1f),
                new NumberTestData("FloatTypeSuffix", "1.2d", 1.2f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3d", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2E-3d", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3d", 1.2e-3f),

                new NumberTestData("FloatTypeSuffix", "1D", 1f),
                new NumberTestData("FloatTypeSuffix", "1.2D", 1.2f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3D", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2E-3D", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3D", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1f", 1f),
                new NumberTestData("FloatTypeSuffix", "1.2f", 1.2f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3f", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2E-3f", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3f", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1F", 1f),
                new NumberTestData("FloatTypeSuffix", "1.2F", 1.2f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3F", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2E-3F", 1.2e-3f),
                new NumberTestData("FloatTypeSuffix", "1.2e-3F", 1.2e-3f),

                new NumberTestData("1", 1f),
                new NumberTestData("1.2", 1.2f),
                new NumberTestData("1.2e+3", 1.2e3f),
                new NumberTestData("1.2E+3", 1.2e3f),
                new NumberTestData("1.2e+3", 1.2e3f),
                new NumberTestData("-1.2e+3", -1.2e3f),
                new NumberTestData("-1.2E-3", -1.2e-3f),
                new NumberTestData("+1.2E+3", 1.2e3f),
                new NumberTestData(" 1.2e3", 1.2e3f),
                new NumberTestData("1.2e3 ", 1.2e3f),
                new NumberTestData("  1.2e3", 1.2e3f),
                new NumberTestData("  -1.2e3", -1.2e3f),
                new NumberTestData("1.2e3  ", 1.2e3f),
                new NumberTestData("   1.2e3   ", 1.2e3f),
                new NumberTestData("1234567890", 1234567890f),
                new NumberTestData("000000000", 0f),
                new NumberTestData("0000.0000", 0f)
        );
    }

    protected List<NumberTestData> createDataForLegalHexStrings() {
        return Arrays.asList(
                new NumberTestData("0xap2", 0xap2f),

                new NumberTestData("FloatTypeSuffix", "0xap2d", 0xap2f),
                new NumberTestData("FloatTypeSuffix", "0xap2D", 0xap2f),
                new NumberTestData("FloatTypeSuffix", "0xap2f", 0xap2f),
                new NumberTestData("FloatTypeSuffix", "0xap2F", 0xap2f),

                new NumberTestData(" 0xap2", 0xap2f),
                new NumberTestData(" 0xap2  ", 0xap2f),
                new NumberTestData("   0xap2   ", 0xap2f),

                new NumberTestData("0x0.1234ab78p0", 0x0.1234ab78p0f),
                new NumberTestData("-0x0.1234AB78p+7", -0x0.1234AB78p7f),
                new NumberTestData("0x1.0p8", 256f),
                new NumberTestData("0x1.234567890abcdefP123", 0x1.234567890abcdefp123f),
                new NumberTestData("+0x1234567890.abcdefp-45", 0x1234567890.abcdefp-45f),
                new NumberTestData("0x1234567890.abcdef1p-45", 0x1234567890.abcdef1p-45f),
                new NumberTestData("0x1234567890.abcdef12p-45", 0x1234567890.abcdef12p-45f),
                new NumberTestData("0x1234567890abcdef1234567890abcdef1234567890abcdef.1234567890abcdefp-45", Float.POSITIVE_INFINITY),
                new NumberTestData("-0x7f00000000000000p-10f", -0x7f00000000000000p-10f),
                new NumberTestData("-0x8f00000000000000p-10f", -0x8f00000000000000p-10f)
        );
    }

    protected List<NumberTestData> createDataForIllegalHexStrings() {
        return Arrays.asList(
                new NumberTestData("0xäp2"),
                new NumberTestData("0x0.1234567äp0")
        );
    }

    protected List<NumberTestData> createFloatTestDataForInputClassesInMethodParseFloatValue() {
        return Arrays.asList(
                new NumberTestData("parseFloatValue(): charOffset too small", "3.14", -1, 4, -1, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charOffset too big", "3.14", 8, 4, 8, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charLength too small", "3.14", 0, -4, 0, -4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charLength too big", "3.14", 0, 8, 0, 8, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): Significand with leading whitespace", "   3", 0, 4, 0, 4, 3f),
                new NumberTestData("parseFloatValue(): Significand with trailing whitespace", "3   ", 0, 4, 0, 4, 3f),
                new NumberTestData("parseFloatValue(): Empty String", "", 0, 0, 0, 0, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Blank String", "   ", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Very long non-blank String", new VirtualCharSequence('a', 66), 0, 66, 0, 66, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Plus Sign", "+", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Negative Sign", "-", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Infinity", "Infinity", 0, 8, 0, 8, Double.POSITIVE_INFINITY),
                new NumberTestData("parseFloatValue(): NaN", "NaN", 0, 3, 0, 3, Double.NaN),
                new NumberTestData("parseInfinity(): Infinit (missing last char)", "Infinit", 0, 7, 0, 7, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseInfinity(): InfinitY (bad last char)", "InfinitY", 0, 8, 0, 8, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNaN(): Na (missing last char)", "Na", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNaN(): Nan (bad last char)", "Nan", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Leading zero", "03", 0, 2, 0, 2, 3f),
                new NumberTestData("parseFloatValue(): Leading zeroes", "003", 0, 3, 0, 3, 3f),
                new NumberTestData("parseFloatValue(): Leading zero x", "0x3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Leading zero X", "0X3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),

                new NumberTestData("parseDecFloatLiteral(): Decimal point only", ".", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With decimal point", "3.", 0, 2, 0, 2, 3f),
                new NumberTestData("parseDecFloatLiteral(): Without decimal point", "3", 0, 1, 0, 1, 3f),
                new NumberTestData("parseDecFloatLiteral(): 7 digits after decimal point", "3.1234567", 0, 9, 0, 9, 3.1234567f),
                new NumberTestData("parseDecFloatLiteral(): 8 digits after decimal point", "3.12345678", 0, 10, 0, 10, 3.12345678f),
                new NumberTestData("parseDecFloatLiteral(): 9 digits after decimal point", "3.123456789", 0, 11, 0, 11, 3.123456789f),
                new NumberTestData("parseDecFloatLiteral(): 1 digit + 7 chars after decimal point", "3.1abcdefg", 0, 10, 0, 10, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'e' at end", "3e", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'E' at end", "3E", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'e' + whitespace at end", "3e   ", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'E' + whitespace  at end", "3E   ", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'e+' at end", "3e+", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'E-' at end", "3E-", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 'e+9' at end", "3e+9", 0, 4, 0, 4, 3e+9),
                new NumberTestData("parseDecFloatLiteral(): With 20 significand digits", "12345678901234567890", 0, 20, 0, 20, 12345678901234567890f),
                new NumberTestData("parseDecFloatLiteral(): With 20 significand digits + non-ascii char", "12345678901234567890￡", 0, 21, 0, 21, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With 20 significand digits with decimal point", "1234567890.1234567890", 0, 21, 0, 21, 1234567890.1234567890f),
                new NumberTestData("parseDecFloatLiteral(): With illegal FloatTypeSuffix 'z': 1.2e3z", "1.2e3z", 0, 6, 0, 6, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecFloatLiteral(): With FloatTypeSuffix 'd': 1.2e3d", "1.2e3d", 0, 6, 0, 6, 1.2e3f),
                new NumberTestData("parseDecFloatLiteral(): With FloatTypeSuffix 'd' + whitespace: 1.2e3d ", "1.2e3d ", 0, 7, 0, 7, 1.2e3f),
                new NumberTestData("parseDecFloatLiteral(): With FloatTypeSuffix 'D': 1.2D", "1.2D", 0, 4, 0, 4, 1.2f),
                new NumberTestData("parseDecFloatLiteral(): With FloatTypeSuffix 'f': 1f", "1f", 0, 2, 0, 2, 1f),
                new NumberTestData("parseDecFloatLiteral(): With FloatTypeSuffix 'F': -1.2e-3F", "-1.2e-3F", 0, 8, 0, 8, -1.2e-3f),
                new NumberTestData("parseDecFloatLiteral(): No digits+whitespace+'z'", ". z", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),

                new NumberTestData("parseHexFloatLiteral(): With decimal point", "0x3.", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): No digits with decimal point", "0x.", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): Without decimal point", "0X3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): 7 digits after decimal point", "0x3.1234567", 0, 11, 0, 11, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): 8 digits after decimal point", "0X3.12345678", 0, 12, 0, 12, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): 9 digits after decimal point", "0x3.123456789", 0, 13, 0, 13, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): 1 digit + 7 chars after decimal point", "0X3.1abcdefg", 0, 12, 0, 12, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'p' at end", "0X3p", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'P' at end", "0x3P", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'p' + whitespace at end", "0X3p   ", 0, 7, 0, 7, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'P' + whitespace  at end", "0x3P   ", 0, 7, 0, 7, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'p+' at end", "0X3p+", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'P-' at end", "0x3P-", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 'p+9' at end", "0X3p+9", 0, 6, 0, 6, 0X3p+9f),
                new NumberTestData("parseHexFloatLiteral(): With 20 significand digits", "0x12345678901234567890p0", 0, 24, 0, 24, 0x12345678901234567890p0f),
                new NumberTestData("parseHexFloatLiteral(): With 20 significand digits + non-ascii char", "0x12345678901234567890￡p0", 0, 25, 0, 25, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With 20 significand digits with decimal point", "0x1234567890.1234567890P0", 0, 25, 0, 25, 0x1234567890.1234567890P0f),
                new NumberTestData("parseHexFloatLiteral(): With illegal FloatTypeSuffix 'z': 0x1.2p3z", "0x1.2p3z", 0, 8, 0, 8, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexFloatLiteral(): With FloatTypeSuffix 'd': 0x1.2p3d", "0x1.2p3d", 0, 8, 0, 8, 0x1.2p3f),
                new NumberTestData("parseHexFloatLiteral(): With FloatTypeSuffix 'd' + whitespace: 0x1.2p3d ", "0x1.2p3d ", 0, 9, 0, 9, 0x1.2p3f),
                new NumberTestData("parseHexFloatLiteral(): With FloatTypeSuffix 'D': 0x1.2p3D", "0x1.2p3D", 0, 8, 0, 8, 0x1.2p3f),
                new NumberTestData("parseHexFloatLiteral(): With FloatTypeSuffix 'f': 0x1.2p3f", "0x1.2p3f", 0, 8, 0, 8, 0x1.2p3f),
                new NumberTestData("parseHexFloatLiteral(): With FloatTypeSuffix 'F': 0x1.2p3F", "0x1.2p3F", 0, 8, 0, 8, 0x1.2p3f)
        );
    }

    protected List<NumberTestData> createDataForLegalCroppedStrings() {
        return Arrays.asList(
                new NumberTestData("x1y", 1d, 1, 1),
                new NumberTestData("xx-0x1p2yyy", -0x1p2f, 2, 6)
        );
    }


    List<NumberTestData> createRegularFloatTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(createTestDataForInfinity());
        list.addAll(createTestDataForNaN());
        list.addAll(createDataForFloatDecimalLimits());
        list.addAll(createDataForFloatHexadecimalLimits());
        list.addAll(createDataForBadStrings());
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForLegalHexStrings());
        list.addAll(createDataForIllegalHexStrings());
        list.addAll(createDataForFloatDecimalClingerInputClasses());
        list.addAll(createDataForFloatHexadecimalClingerInputClasses());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createFloatTestDataForInputClassesInMethodParseFloatValue());
        list.addAll(createDataForSignificandDigitsInputClasses());
        return list;
    }

    Stream<NumberTestData> createLongRunningFloatTestData() {
        Stream<NumberTestData> s = Stream.empty();
        if (longRunningTests) {
            s = Stream.concat(s, createDataWithVeryLongInputStrings().stream());
        }
        return s;
    }

}