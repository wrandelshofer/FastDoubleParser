/*
 * @(#)JavaDoubleParserDataSource.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JsonDoubleParserDataSource {
    private static final boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));
    private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 4;

    public static Stream<NumberTestData> parseDouble_CharSequence() {
        return createRegularDoubleTestData().stream().filter(d -> d.charOffset() == 0 && d.charLength() == d.input().length());
    }

    public static Stream<NumberTestData> parseDouble_CharSequence_int_int() {
        return createRegularDoubleTestData().stream().filter(d -> d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_byteArray() {
        return createRegularDoubleTestData().stream().filter(d -> d.charOffset() == 0 && d.byteLength() == d.input().length() && d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_byteArray_int_int() {
        return createRegularDoubleTestData().stream().filter(d -> d.byteLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_charArray() {
        return createRegularDoubleTestData().stream().filter(d -> d.charOffset() == 0 && d.charLength() == d.input().length() && d.charLength() < MAX_ARRAY_LENGTH);
    }

    public static Stream<NumberTestData> parseDouble_charArray_int_int() {
        return createRegularDoubleTestData().stream().filter(d -> d.charLength() < MAX_ARRAY_LENGTH);
    }


    public static List<NumberTestData> createFloatTestDataForInputClassesInMethodParseFloatValue() {
        return Arrays.asList(
                new NumberTestData("parseFloatValue(): charOffset too small", "3.14", -1, 4, -1, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charOffset too big", "3.14", 8, 4, 8, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charLength too small", "3.14", 0, -4, 0, -4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): charLength too big", "3.14", 0, 8, 0, 8, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseFloatValue(): Empty String", "", 0, 0, 0, 0, AbstractNumberParser.EMPTY_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Blank String", "   ", 0, 3, 0, 3, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Very long non-blank String", new VirtualCharSequence('a', 66), 0, 66, 0, 66, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Plus Sign", "+", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Negative Sign", "-", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseInfinity(): Infinit (missing last char)", "Infinit", 0, 7, 0, 7, AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("parseInfinity(): InfinitY (bad last char)", "InfinitY", 0, 8, 0, 8, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNaN(): Na (missing last char)", "Na", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNaN(): Nan (bad last char)", "Nan", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseFloatValue(): Leading zero 0e3", "0e3", 0),
                new NumberTestData("parseFloatValue(): Leading zero 03", "03"),
                new NumberTestData("parseFloatValue(): Leading zeroes", "003"),
                new NumberTestData("parseFloatValue(): Leading zero x", "0x3"),
                new NumberTestData("parseFloatValue(): Leading zero X", "0X3")
        );
    }

    public static List<NumberTestData> createFloatTestDataForInputClassesInMethodParseDecLiteral() {
        return Arrays.asList(
                new NumberTestData("parseDecLiteral(): Decimal point only", ".", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): With decimal point", "3.", 0, 2, 0, 2, 3d),
                new NumberTestData("parseDecLiteral(): Without decimal point", "3", 0, 1, 0, 1, 3d),
                new NumberTestData("parseDecLiteral(): 7 digits after decimal point", "3.1234567", 0, 9, 0, 9, 3.1234567),
                new NumberTestData("parseDecLiteral(): 8 digits after decimal point", "3.12345678", 0, 10, 0, 10, 3.12345678),
                new NumberTestData("parseDecLiteral(): 9 digits after decimal point", "3.123456789", 0, 11, 0, 11, 3.123456789),
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
                new NumberTestData("parseDecLiteral(): With 20 significand digits with decimal point", "1234567890.1234567890", 0, 21, 0, 21, 1234567890.1234567890),
                new NumberTestData("parseDecLiteral(): With illegal FloatTypeSuffix 'z': 1.2e3z", "1.2e3z", 0, 6, 0, 6, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseDecLiteral(): No digits+whitespace+'z'", ". z", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)
        );
    }


    public static List<NumberTestData> createDataForLegalCroppedStrings() {
        return Arrays.asList(
                new NumberTestData("x1y", 1d, 1, 1)
        );
    }

    public static List<NumberTestData> createRegularDoubleTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(JsonGenericFloatingPointDataSource.createTestDataForInfinity());
        list.addAll(JsonGenericFloatingPointDataSource.createTestDataForNaN());
        list.addAll(GenericDoubleDataSource.createDataForDoubleDecimalLimits());
        list.addAll(GenericFloatingPointDataSource.createDataForIllegalStrings());
        list.addAll(GenericDoubleDataSource.createDataForLegalDoubleDecStrings());
        list.addAll(JsonGenericFloatingPointDataSource.createDataForIllegalDecStringsWithWhitespace());
        list.addAll(GenericDoubleDataSource.createDataForDoubleDecimalClingerInputClasses());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createFloatTestDataForInputClassesInMethodParseFloatValue());
        list.addAll(createFloatTestDataForInputClassesInMethodParseDecLiteral());
        list.addAll(GenericDoubleDataSource.createDataForDoubleSignificandDigitsInputClasses());
        if (longRunningTests) {
            list.addAll(GenericFloatDataSource.createDataWithVeryLongInputStrings());
        }
        return list;
    }
}
