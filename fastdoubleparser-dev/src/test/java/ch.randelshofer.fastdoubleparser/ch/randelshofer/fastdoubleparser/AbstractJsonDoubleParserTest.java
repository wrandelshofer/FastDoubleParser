/*
 * @(#)AbstractJsonDoubleParserTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractJsonDoubleParserTest extends AbstractFloatValueParserTest {
    public final static int EXPECTED_MAX_INPUT_LENGTH = Integer.MAX_VALUE - 4;
    private boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));

    protected List<NumberTestData> createDataForBadStrings() {
        return Arrays.asList(
                new NumberTestData("empty", "", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("00"),
                new NumberTestData("007"),
                new NumberTestData("000000000"),
                new NumberTestData("0000.0000"),
                new NumberTestData("+"),
                new NumberTestData("+1"),
                new NumberTestData("+1.2"),
                new NumberTestData("+1.2e3"),
                new NumberTestData("+1.2E3"),
                new NumberTestData("+1.2e3"),
                new NumberTestData("-"),
                new NumberTestData("+e"),
                new NumberTestData("-e"),
                new NumberTestData("+e123"),
                new NumberTestData("+1.2E+3"),

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
                new NumberTestData("0xp89"),
                new NumberTestData("FloatTypeSuffix", "1d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2E-3d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3d", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),

                new NumberTestData("FloatTypeSuffix", "1D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2E-3D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3D", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2E-3f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3f", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2E-3F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("FloatTypeSuffix", "1.2e-3F", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),

                new NumberTestData("  1.2e3"),
                new NumberTestData("  -1.2e3"),
                new NumberTestData(" 1.2e3"),
                new NumberTestData("1.2e3 "),
                new NumberTestData("1.2e3  "),
                new NumberTestData("   1.2e3   ")
        );
    }

    protected List<NumberTestData> createDataForLegalDecStrings() {
        return Arrays.asList(
                new NumberTestData("0", 0),
                new NumberTestData("1", 1),
                new NumberTestData("1.2", 1.2),
                new NumberTestData("1.2e3", 1.2e3),
                new NumberTestData("1.2E3", 1.2e3),
                new NumberTestData("1.2e3", 1.2e3),
                new NumberTestData("-1", -1),
                new NumberTestData("-1.2", -1.2),
                new NumberTestData("-1.2e3", -1.2e3),
                new NumberTestData("-1.2E3", -1.2e3),
                new NumberTestData("-1.2e3", -1.2e3),
                new NumberTestData("1", 1),
                new NumberTestData("1.2", 1.2),
                new NumberTestData("1.2e-3", 1.2e-3),
                new NumberTestData("1.2E-3", 1.2e-3),
                new NumberTestData("1.2e-3", 1.2e-3),

                new NumberTestData("1", 1),
                new NumberTestData("1.2", 1.2),
                new NumberTestData("1.2e+3", 1.2e3),
                new NumberTestData("1.2E+3", 1.2e3),
                new NumberTestData("1.2e+3", 1.2e3),
                new NumberTestData("-1.2e+3", -1.2e3),
                new NumberTestData("-1.2E-3", -1.2e-3),
                new NumberTestData("1234567890", 1234567890d)
        );
    }


    List<NumberTestData> createDataForLegalCroppedStrings() {
        return Arrays.asList(
                new NumberTestData("x1y", 1d, 1, 1)
        );
    }

    protected List<NumberTestData> createFloatTestDataForInputClassesInMethodParseNumber() {
        return Arrays.asList(
                new NumberTestData("parseNumber(): charOffset too small", "3.14", -1, 4, -1, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseNumber(): charOffset too big", "3.14", 8, 4, 8, 4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseNumber(): charLength too small", "3.14", 0, -4, 0, -4, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseNumber(): charLength too big", "3.14", 0, 8, 0, 8, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("parseNumber(): Significand with leading whitespace", "   3", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Significand with trailing whitespace", "3   ", 0, 4, 0, 4, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Empty String", "", 0, 0, 0, 0, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Blank String", "   ", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Very long non-blank String", new VirtualCharSequence('a', 66), 0, 66, 0, 66, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Plus Sign", "+", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Negative Sign", "-", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Infinity", "Infinity", 0, 8, 0, 8, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): NaN", "NaN", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Leading zero", "03", 0, 2, 0, 2, 3d),
                new NumberTestData("parseNumber(): Leading zeroes", "003", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Leading zero x", "0x3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): Leading zero X", "0X3", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),

                new NumberTestData("parseNumber(): Decimal point only", ".", 0, 1, 0, 1, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With decimal point", "3.", 0, 2, 0, 2, 3d),
                new NumberTestData("parseNumber(): Without decimal point", "3", 0, 1, 0, 1, 3d),
                new NumberTestData("parseNumber(): 7 digits after decimal point", "3.1234567", 0, 9, 0, 9, 3.1234567),
                new NumberTestData("parseNumber(): 8 digits after decimal point", "3.12345678", 0, 10, 0, 10, 3.12345678),
                new NumberTestData("parseNumber(): 9 digits after decimal point", "3.123456789", 0, 11, 0, 11, 3.123456789),
                new NumberTestData("parseNumber(): 1 digit + 7 chars after decimal point", "3.1abcdefg", 0, 10, 0, 10, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'e' at end", "3e", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'E' at end", "3E", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'e' + whitespace at end", "3e   ", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'E' + whitespace  at end", "3E   ", 0, 5, 0, 5, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'e+' at end", "3e+", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'E-' at end", "3E-", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 'e+9' at end", "3e+9", 0, 4, 0, 4, 3e+9),
                new NumberTestData("parseNumber(): With 20 significand digits", "12345678901234567890", 0, 20, 0, 20, 12345678901234567890d),
                new NumberTestData("parseNumber(): With 20 significand digits + non-ascii char", "12345678901234567890￡", 0, 21, 0, 21, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With 20 significand digits with decimal point", "1234567890.1234567890", 0, 21, 0, 21, 1234567890.1234567890),
                new NumberTestData("parseNumber(): With illegal FloatTypeSuffix 'z': 1.2e3z", "1.2e3z", 0, 6, 0, 6, 10, 1.2e3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With FloatTypeSuffix 'd': 1.2e3d", "1.2e3d", 0, 6, 0, 6, 10, 1.2e3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With FloatTypeSuffix 'd' + whitespace: 1.2e3d ", "1.2e3d ", 0, 7, 0, 7, 10, 1.2e3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With FloatTypeSuffix 'D': 1.2D", "1.2D", 0, 4, 0, 4, 10, 1.2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With FloatTypeSuffix 'f': 1f", "1f", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): With FloatTypeSuffix 'F': -1.2e-3F", "-1.2e-3F", 0, 8, 0, 8, 10, -1.2e-3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("parseNumber(): No digits+whitespace+'z'", ". z", 0, 2, 0, 2, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)

        );
    }

    List<NumberTestData> createRegularTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(createDataForDoubleDecimalLimits());
        list.addAll(createDataForBadStrings());
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForDoubleDecimalClingerInputClasses());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createFloatTestDataForInputClassesInMethodParseNumber());
        return list;
    }

    protected List<NumberTestData> createDataWithVeryLongInputStrings() {
        return Arrays.asList(
                new NumberTestData("too many input characters", new VirtualCharSequence('1', Integer.MAX_VALUE - 3), AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class)
        );
    }

    List<NumberTestData> createLongRunningTestData() {
        List<NumberTestData> list = new ArrayList<>();
        if (longRunningTests) {
            list.addAll(createDataWithVeryLongInputStrings());
        }
        return list;
    }
}