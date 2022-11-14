/*
 * @(#)AbstractFastXParserTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractJsonFloatValueParserTest extends AbstractFloatValueParserTest {

    protected List<FloatTestData> createDataForBadStrings() {
        return Arrays.asList(
                new FloatTestData("empty", ""),
                new FloatTestData("00"),
                new FloatTestData("007"),
                new FloatTestData("000000000"),
                new FloatTestData("0000.0000"),
                new FloatTestData("+"),
                new FloatTestData("+1"),
                new FloatTestData("+1.2"),
                new FloatTestData("+1.2e3"),
                new FloatTestData("+1.2E3"),
                new FloatTestData("+1.2e3"),
                new FloatTestData("-"),
                new FloatTestData("+e"),
                new FloatTestData("-e"),
                new FloatTestData("+e123"),
                new FloatTestData("+1.2E+3"),

                new FloatTestData("-e456"),
                new FloatTestData("78 e9"),
                new FloatTestData("-01 e23"),
                new FloatTestData("- 1"),
                new FloatTestData("-0 .5"),
                new FloatTestData("-0. 5"),
                new FloatTestData("-0.5 e"),
                new FloatTestData("-0.5e 3"),
                new FloatTestData("45\ne6"),
                new FloatTestData("d"),
                new FloatTestData(".f"),
                new FloatTestData("7_8e90"),
                new FloatTestData("12e3_4"),
                new FloatTestData("00x5.6p7"),
                new FloatTestData("89p0"),
                new FloatTestData("cafebabe.1p2"),
                new FloatTestData("0x123pa"),
                new FloatTestData("0x1.2e7"),
                new FloatTestData("0xp89"),
                new FloatTestData("FloatTypeSuffix", "1d"),
                new FloatTestData("FloatTypeSuffix", "1.2d"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3d"),
                new FloatTestData("FloatTypeSuffix", "1.2E-3d"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3d"),

                new FloatTestData("FloatTypeSuffix", "1D"),
                new FloatTestData("FloatTypeSuffix", "1.2D"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3D"),
                new FloatTestData("FloatTypeSuffix", "1.2E-3D"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3D"),
                new FloatTestData("FloatTypeSuffix", "1f"),
                new FloatTestData("FloatTypeSuffix", "1.2f"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3f"),
                new FloatTestData("FloatTypeSuffix", "1.2E-3f"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3f"),
                new FloatTestData("FloatTypeSuffix", "1F"),
                new FloatTestData("FloatTypeSuffix", "1.2F"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3F"),
                new FloatTestData("FloatTypeSuffix", "1.2E-3F"),
                new FloatTestData("FloatTypeSuffix", "1.2e-3F"),

                new FloatTestData("  1.2e3"),
                new FloatTestData("  -1.2e3"),
                new FloatTestData(" 1.2e3"),
                new FloatTestData("1.2e3 "),
                new FloatTestData("1.2e3  "),
                new FloatTestData("   1.2e3   ")
        );
    }

    protected List<FloatTestData> createDataForLegalDecStrings() {
        return Arrays.asList(
                new FloatTestData("0", 0, 0f),
                new FloatTestData("1", 1, 1f),
                new FloatTestData("1.2", 1.2, 1.2f),
                new FloatTestData("1.2e3", 1.2e3, 1.2e3f),
                new FloatTestData("1.2E3", 1.2e3, 1.2e3f),
                new FloatTestData("1.2e3", 1.2e3, 1.2e3f),
                new FloatTestData("-1", -1, -1f),
                new FloatTestData("-1.2", -1.2, -1.2f),
                new FloatTestData("-1.2e3", -1.2e3, -1.2e3f),
                new FloatTestData("-1.2E3", -1.2e3, -1.2e3f),
                new FloatTestData("-1.2e3", -1.2e3, -1.2e3f),
                new FloatTestData("1", 1, 1f),
                new FloatTestData("1.2", 1.2, 1.2f),
                new FloatTestData("1.2e-3", 1.2e-3, 1.2e-3f),
                new FloatTestData("1.2E-3", 1.2e-3, 1.2e-3f),
                new FloatTestData("1.2e-3", 1.2e-3, 1.2e-3f),

                new FloatTestData("1", 1, 1f),
                new FloatTestData("1.2", 1.2, 1.2f),
                new FloatTestData("1.2e+3", 1.2e3, 1.2e3f),
                new FloatTestData("1.2E+3", 1.2e3, 1.2e3f),
                new FloatTestData("1.2e+3", 1.2e3, 1.2e3f),
                new FloatTestData("-1.2e+3", -1.2e3, -1.2e3f),
                new FloatTestData("-1.2E-3", -1.2e-3, -1.2e-3f),
                new FloatTestData("1234567890", 1234567890d, 1234567890f)
        );
    }


    List<FloatTestData> createDataForLegalCroppedStrings() {
        return Arrays.asList(
                new FloatTestData("x1y", 1, 1f, 1, 1)
        );
    }

    protected List<FloatTestData> createFloatTestDataForInputClassesInMethodParseNumber() {
        return Arrays.asList(
                new FloatTestData("parseNumber(): charOffset too small", "3.14", -1, 4, -1, 4, 3d, 3f, false),
                new FloatTestData("parseNumber(): charOffset too big", "3.14", 8, 4, 8, 4, 3d, 3f, false),
                new FloatTestData("parseNumber(): charLength too small", "3.14", 0, -4, 0, -4, 3d, 3f, false),
                new FloatTestData("parseNumber(): charLength too big", "3.14", 0, 8, 0, 8, 3d, 3f, false),
                new FloatTestData("parseNumber(): Significand with leading whitespace", "   3", 0, 4, 0, 4, 3d, 3f, false),
                new FloatTestData("parseNumber(): Significand with trailing whitespace", "3   ", 0, 4, 0, 4, 3d, 3f, false),
                new FloatTestData("parseNumber(): Empty String", "", 0, 0, 0, 0, 0d, 0f, false),
                new FloatTestData("parseNumber(): Blank String", "   ", 0, 3, 0, 3, 0d, 0f, false),
                new FloatTestData("parseNumber(): Very long non-blank String", "a".repeat(66), 0, 66, 0, 66, 0d, 0f, false),
                new FloatTestData("parseNumber(): Plus Sign", "+", 0, 1, 0, 1, 0d, 0f, false),
                new FloatTestData("parseNumber(): Negative Sign", "-", 0, 1, 0, 1, 0d, 0f, false),
                new FloatTestData("parseNumber(): Infinity", "Infinity", 0, 8, 0, 8, Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, false),
                new FloatTestData("parseNumber(): NaN", "NaN", 0, 3, 0, 3, Double.NaN, Float.NaN, false),
                new FloatTestData("parseNumber(): Leading zero", "03", 0, 2, 0, 2, 3d, 3f, true),
                new FloatTestData("parseNumber(): Leading zeroes", "003", 0, 3, 0, 3, 3d, 3f, false),
                new FloatTestData("parseNumber(): Leading zero x", "0x3", 0, 3, 0, 3, 0d, 0f, false),
                new FloatTestData("parseNumber(): Leading zero X", "0X3", 0, 3, 0, 3, 0d, 0f, false),

                new FloatTestData("parseNumber(): Decimal point only", ".", 0, 1, 0, 1, 0d, 0f, false),
                new FloatTestData("parseNumber(): With decimal point", "3.", 0, 2, 0, 2, 3d, 3f, true),
                new FloatTestData("parseNumber(): Without decimal point", "3", 0, 1, 0, 1, 3d, 3f, true),
                new FloatTestData("parseNumber(): 7 digits after decimal point", "3.1234567", 0, 9, 0, 9, 3.1234567, 3.1234567f, true),
                new FloatTestData("parseNumber(): 8 digits after decimal point", "3.12345678", 0, 10, 0, 10, 3.12345678, 3.12345678f, true),
                new FloatTestData("parseNumber(): 9 digits after decimal point", "3.123456789", 0, 11, 0, 11, 3.123456789, 3.123456789f, true),
                new FloatTestData("parseNumber(): 1 digit + 7 chars after decimal point", "3.1abcdefg", 0, 10, 0, 10, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'e' at end", "3e", 0, 2, 0, 2, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'E' at end", "3E", 0, 2, 0, 2, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'e' + whitespace at end", "3e   ", 0, 5, 0, 5, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'E' + whitespace  at end", "3E   ", 0, 5, 0, 5, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'e+' at end", "3e+", 0, 3, 0, 3, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'E-' at end", "3E-", 0, 3, 0, 3, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 'e+9' at end", "3e+9", 0, 4, 0, 4, 3e+9, 3e+9f, true),
                new FloatTestData("parseNumber(): With 20 significand digits", "12345678901234567890", 0, 20, 0, 20, 12345678901234567890d, 12345678901234567890f, true),
                new FloatTestData("parseNumber(): With 20 significand digits + non-ascii char", "12345678901234567890￡", 0, 21, 0, 21, 0d, 0f, false),
                new FloatTestData("parseNumber(): With 20 significand digits with decimal point", "1234567890.1234567890", 0, 21, 0, 21, 1234567890.1234567890, 1234567890.1234567890f, true),
                new FloatTestData("parseNumber(): With illegal FloatTypeSuffix 'z': 1.2e3z", "1.2e3z", 0, 6, 0, 6, 1.2e3, 1.2e3f, false),
                new FloatTestData("parseNumber(): With FloatTypeSuffix 'd': 1.2e3d", "1.2e3d", 0, 6, 0, 6, 1.2e3, 1.2e3f, false),
                new FloatTestData("parseNumber(): With FloatTypeSuffix 'd' + whitespace: 1.2e3d ", "1.2e3d ", 0, 7, 0, 7, 1.2e3, 1.2e3f, false),
                new FloatTestData("parseNumber(): With FloatTypeSuffix 'D': 1.2D", "1.2D", 0, 4, 0, 4, 1.2, 1.2f, false),
                new FloatTestData("parseNumber(): With FloatTypeSuffix 'f': 1f", "1f", 0, 2, 0, 2, 1d, 1f, false),
                new FloatTestData("parseNumber(): With FloatTypeSuffix 'F': -1.2e-3F", "-1.2e-3F", 0, 8, 0, 8, -1.2e-3, -1.2e-3f, false),
                new FloatTestData("parseNumber(): No digits+whitespace+'z'", ". z", 0, 2, 0, 2, 0d, 0f, false)

        );
    }

    List<FloatTestData> createAllTestData() {
        List<FloatTestData> list = new ArrayList<>();
        list.addAll(createDataForDecimalLimits());
        list.addAll(createDataForBadStrings());
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForDecimalClingerInputClasses());
        list.addAll(createDataForLegalCroppedStrings());
        list.addAll(createFloatTestDataForInputClassesInMethodParseNumber());
        return list;
    }

}