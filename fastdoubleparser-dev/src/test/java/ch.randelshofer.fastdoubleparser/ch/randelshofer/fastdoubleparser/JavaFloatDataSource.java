/*
 * @(#)JavaDoubleParserDataSource.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;


public class JavaFloatDataSource {
    private static final boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));
    private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 4;

    public static Stream<NumberTestData> parseFloat_CharSequence() {
        return createRegularFloatTestData().stream().filter(d -> d.charOffset() == 0 && d.charLength() == d.input().length());
    }

    public static Stream<NumberTestData> parseFloat_CharSequence_int_int() {
        return createRegularFloatTestData().stream().filter(d -> d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseFloat_byteArray() {
        return createRegularFloatTestData().stream().filter(d -> d.charOffset() == 0 && d.byteLength() == d.input().length() && d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseFloat_byteArray_int_int() {
        return createRegularFloatTestData().stream().filter(d -> d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseFloat_charArray() {
        return createRegularFloatTestData().stream().filter(d -> d.charOffset() == 0 && d.charLength() == d.input().length() && d.charLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseFloat_charArray_int_int() {
        return createRegularFloatTestData().stream().filter(d -> d.charLength() < MAX_ARRAY_LENGTH);
    }


    public static List<NumberTestData> createDataForLegalDecStringsWithFloatTypeSuffix() {
        return Arrays.asList(
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix 'D': 1.2D", "1.2D", 0, 4, 0, 4, 1.2f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix 'F': -1.2e-3F", "-1.2e-3F", 0, 8, 0, 8, -1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix 'd' + whitespace: 1.2e3d ", "1.2e3d ", 0, 7, 0, 7, 1.2e3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix 'd': 1.2e3d", "1.2e3d", 0, 6, 0, 6, 1.2e3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix 'f': 1f", "1f", 0, 2, 0, 2, 1f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2D", 1.2f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2E-3D", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2E-3F", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2E-3d", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2E-3f", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2F", 1.2f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2d", 1.2f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2e-3D", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2e-3F", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2e-3d", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2e-3f", 1.2e-3f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1.2f", 1.2f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1D", 1f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1F", 1f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1d", 1f),
                new NumberTestData("parseDecLiteral(): With FloatTypeSuffix", "1f", 1f),
                new NumberTestData("parseHexLiteral(): With FloatTypeSuffix 'D': 0x1.2p3D", "0x1.2p3D", 0, 8, 0, 8, 0x1.2p3f),
                new NumberTestData("parseHexLiteral(): With FloatTypeSuffix 'F': 0x1.2p3F", "0x1.2p3F", 0, 8, 0, 8, 0x1.2p3f),
                new NumberTestData("parseHexLiteral(): With FloatTypeSuffix 'd' + whitespace: 0x1.2p3d ", "0x1.2p3d ", 0, 9, 0, 9, 0x1.2p3f),
                new NumberTestData("parseHexLiteral(): With FloatTypeSuffix 'd': 0x1.2p3d", "0x1.2p3d", 0, 8, 0, 8, 0x1.2p3f),
                new NumberTestData("parseHexLiteral(): With FloatTypeSuffix 'f': 0x1.2p3f", "0x1.2p3f", 0, 8, 0, 8, 0x1.2p3f)
        );
    }


    public static List<NumberTestData> createDataForLegalHexStrings() {
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

    public static List<NumberTestData> createDataForIllegalHexStrings() {
        return Arrays.asList(
                new NumberTestData("0xäp2"),
                new NumberTestData("0x/äp2"),
                new NumberTestData("0x0.1234567äp0"),
                new NumberTestData("0x0.Łp0"),//LATIN CAPITAL LETTER L WITH STROKE U+0141 must not be confused with 'A' U+0041
                new NumberTestData("0x0.Ł1234567p0")//LATIN CAPITAL LETTER L WITH STROKE U+0141 must not be confused with 'A' U+0041
        );
    }

    public static List<NumberTestData> createFloatTestDataForInputClassesInMethodParseFloatValue() {
        return Arrays.asList(
                new NumberTestData("parseFloatValue(): charOffset too small", "3.14", -1, 4, -1, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charOffset too big", "3.14", 8, 4, 8, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charLength too small", "3.14", 0, -4, 0, -4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charLength too big", "3.14", 0, 8, 0, 8, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): Significand with leading whitespace", "   3", 0, 4, 0, 4, 3f),
                new NumberTestData("parseFloatValue(): Significand with trailing whitespace", "3   ", 0, 4, 0, 4, 3f),
                new NumberTestData("parseFloatValue(): Empty String", "", 0, 0, 0, 0, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Blank String", "   ", 0, 3, 0, 3, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Very long non-blank String", new VirtualCharSequence('a', 66), 0, 66, 0, 66, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Plus Sign", "+", 0, 1, 0, 1, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): +1", "+1", 1f),
                new NumberTestData("parseFloatValue(): +1.2", "+1.2", 1.2f),
                new NumberTestData("parseFloatValue(): +1.2e3", "+1.2e3", 1.2e3f),
                new NumberTestData("parseFloatValue(): +1.2E3", "+1.2E3", 1.2e3f),
                new NumberTestData("parseFloatValue(): +1.2E+3", "+1.2E+3", 1.2e3f),
                new NumberTestData("parseFloatValue(): 000000000", "000000000", 0f),
                new NumberTestData("parseFloatValue(): 0000.0000", "0000.0000", 0f),
                new NumberTestData("parseFloatValue(): Negative Sign", "-", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Infinity", "Infinity", 0, 8, 0, 8, Double.POSITIVE_INFINITY),
                new NumberTestData("parseFloatValue(): NaN", "NaN", 0, 3, 0, 3, Double.NaN),
                new NumberTestData("parseInfinity(): Infinit (missing last char)", "Infinit", 0, 7, 0, 7, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseInfinity(): InfinitY (bad last char)", "InfinitY", 0, 8, 0, 8, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNaN(): Na (missing last char)", "Na", 0, 2, 0, 2, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseNaN(): Nan (bad last char)", "Nan", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Leading zero", "03", 0, 2, 0, 2, 3f),
                new NumberTestData("parseFloatValue(): Leading zeroes", "003", 0, 3, 0, 3, 3f),
                new NumberTestData("parseFloatValue(): Leading zero x", "0x3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Leading zero X", "0X3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)
        );
    }

    public static List<NumberTestData> createFloatTestDataForInputClassesInMethodParseDecLiteral() {
        return Arrays.asList(
                new NumberTestData("parseDecLiteral(): Decimal point only", ".", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With decimal point", "3.", 0, 2, 0, 2, 3f),
                new NumberTestData("parseDecLiteral(): Without decimal point", "3", 0, 1, 0, 1, 3f),
                new NumberTestData("parseDecLiteral(): 7 digits after decimal point", "3.1234567", 0, 9, 0, 9, 3.1234567f),
                new NumberTestData("parseDecLiteral(): 8 digits after decimal point", "3.12345678", 0, 10, 0, 10, 3.12345678f),
                new NumberTestData("parseDecLiteral(): 9 digits after decimal point", "3.123456789", 0, 11, 0, 11, 3.123456789f),
                new NumberTestData("parseDecLiteral(): 1 digit + 7 chars after decimal point", "3.1abcdefg", 0, 10, 0, 10, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'e' at end", "3e", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'E' at end", "3E", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'e' + whitespace at end", "3e   ", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'E' + whitespace  at end", "3E   ", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'e+' at end", "3e+", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'E-' at end", "3E-", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 'e+9' at end", "3e+9", 0, 4, 0, 4, 3e+9),
                new NumberTestData("parseDecLiteral(): With 20 significand digits (truncated)", "12345678901234567890", 12345678901234567890d),
                new NumberTestData("parseDecLiteral(): With 20 significand digits (not truncated)", "0.00012345678901234567", 0.00012345678901234567),
                new NumberTestData("parseDecLiteral(): With 20 significand digits + non-ascii char", "12345678901234567890￡", 0, 21, 0, 21, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With 20 significand digits with decimal point", "1234567890.1234567890", 0, 21, 0, 21, 1234567890.1234567890f),
                new NumberTestData("parseDecLiteral(): No digits+whitespace+'z'", ". z", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)

        );
    }

    public static List<NumberTestData> createFloatTestDataForInputClassesInMethodParseHexLiteral() {
        return Arrays.asList(
                new NumberTestData("parseHexLiteral(): With decimal point", "0x3.", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): No digits with decimal point", "0x.", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): Without decimal point", "0X3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With two decimal points", "0X3..4"),
                new NumberTestData("parseHexLiteral(): 7 digits after decimal point", "0x3.1234567", 0, 11, 0, 11, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): 8 digits after decimal point", "0X3.12345678", 0, 12, 0, 12, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): 9 digits after decimal point", "0x3.123456789", 0, 13, 0, 13, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): 1 digit + 7 chars after decimal point", "0X3.1abcdefg", 0, 12, 0, 12, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'p' at end", "0X3p", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'P' at end", "0x3P", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'p' + whitespace at end", "0X3p   ", 0, 7, 0, 7, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'P' + whitespace  at end", "0x3P   ", 0, 7, 0, 7, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'p+' at end", "0X3p+", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'P-' at end", "0x3P-", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 'p+9' at end", "0X3p+9", 0, 6, 0, 6, 0X3p+9f),
                new NumberTestData("parseHexLiteral(): With exponent overflow", "0x1p12345", Double.POSITIVE_INFINITY),
                new NumberTestData("parseHexLiteral(): With 20 significand digits (truncated)", "0x12345678901234567890p0", 0, 24, 0, 24, 0x12345678901234567890p0f),
                new NumberTestData("parseHexLiteral(): With 20 significand digits (not truncated)", "0x00000de0b6b3a7640000p0", 0, 24, 0, 24, 0x00000de0b6b3a7640000p0f),
                new NumberTestData("parseHexLiteral(): With 20 significand digits + non-ascii char", "0x12345678901234567890￡p0", 0, 25, 0, 25, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseHexLiteral(): With 20 significand digits with decimal point", "0x1234567890.1234567890P0", 0, 25, 0, 25, 0x1234567890.1234567890P0f),
                new NumberTestData("parseHexLiteral(): With illegal FloatTypeSuffix 'z': 0x1.2p3z", "0x1.2p3z", 0, 8, 0, 8, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)

        );
    }


    public static List<NumberTestData> createDataForLegalCroppedStrings() {
        return Arrays.asList(
                new NumberTestData("x1y", 1d, 1, 1)
        );
    }


    public static List<NumberTestData> createDataForLegalCroppedStringsHex() {
        return Arrays.asList(
                new NumberTestData("xx-0x1p2yyy", -0x1p2f, 2, 6)
        );
    }


    public static List<NumberTestData> createRegularFloatTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(JavaFloatingPointDataSource.createTestDataForInfinity());
        list.addAll(JavaFloatingPointDataSource.createTestDataForNaN());
        list.addAll(GenericFloatDataSource.createDataForFloatDecimalLimits());
        list.addAll(GenericFloatDataSource.createDataForFloatHexadecimalLimits());
        list.addAll(GenericFloatingPointDataSource.createDataForIllegalStrings());
        list.addAll(GenericFloatDataSource.createDataForLegalFloatDecStrings());
        list.addAll(JavaFloatingPointDataSource.createDataForLegalDecStringsWithWhitespace());
        list.addAll(JavaFloatingPointDataSource.createDataForIllegalDecStringsWithWhitespace());
        list.addAll(JavaFloatingPointDataSource.createDataForIllegalHexStrings());
        list.addAll(createDataForLegalDecStringsWithFloatTypeSuffix());
        list.addAll(createDataForLegalHexStrings());
        list.addAll(createDataForIllegalHexStrings());
        list.addAll(GenericFloatDataSource.createDataForFloatDecimalClingerInputClasses());
        list.addAll(GenericFloatDataSource.createDataForFloatHexadecimalClingerInputClasses());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createDataForLegalCroppedStringsHex());
        list.addAll(createFloatTestDataForInputClassesInMethodParseFloatValue());
        list.addAll(createFloatTestDataForInputClassesInMethodParseDecLiteral());
        list.addAll(createFloatTestDataForInputClassesInMethodParseHexLiteral());
        list.addAll(GenericFloatDataSource.createDataForFloatSignificandDigitsInputClasses());
        if (longRunningTests) {
            list.addAll(GenericFloatDataSource.createDataWithVeryLongInputStrings());
        }

        // Check for duplicate entries
        LinkedHashSet<NumberTestData> set = new LinkedHashSet<NumberTestData>();
        for (int i = 0; i < list.size(); i++) {
            if (!set.add(list.get(i))) {
                System.err.println(JavaFloatDataSource.class + " [" + (i + 1) + "] " + list.get(i) + " is a duplicate");
            }
        }
        if (set.size() != list.size()) {
            throw new AssertionError(JavaFloatDataSource.class + " list contains duplicates");
        }
        return list;
    }
}
