/*
 * @(#)JavaGenericFloatingPointParserDataSource.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;
import java.util.List;

public class JavaFloatingPointDataSource {
    public static List<NumberTestData> createTestDataForInfinity() {
        return Arrays.asList(
                new NumberTestData("Infinity", Double.POSITIVE_INFINITY),
                new NumberTestData("+Infinity", Double.POSITIVE_INFINITY),
                new NumberTestData("-Infinity", Double.NEGATIVE_INFINITY),
                new NumberTestData("Infinit").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("INfinity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("InFinity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("InfInity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("InfiNity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("InfinIty").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("InfiniTy").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("InfinitY").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+ Infinity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+.Infinity").withException(AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("eInfinity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("5Infinity").withException(AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("0Infinity").withException(AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData(".Infinity").withException(AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("+Infinityf").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-InfinityF").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+Infinityd").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+-InfinityD").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+InfinityNaN").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("infinity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class)
        );
    }

    public static List<NumberTestData> createTestDataForNaN() {
        return Arrays.asList(
                new NumberTestData("NaN", Double.NaN),
                new NumberTestData("+NaN", Double.NaN),
                new NumberTestData("-NaN", Double.NaN),
                new NumberTestData("NaNf").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+NaNd").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-NaNF").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+-NaND").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("NaNInfinity").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("nan").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("Non").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("Nan").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("NaNNy").withException(AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class)
        );
    }


    public static List<NumberTestData> createDataForLegalDecStringsWithWhitespace() {
        return Arrays.asList(
                new NumberTestData("Significand with leading whitespace", "   3", 0, 4, 0, 4, 3d),
                new NumberTestData("Significand with trailing whitespace", "3   ", 0, 4, 0, 4, 3d),
                new NumberTestData(" 1.2e3", 1.2e3),
                new NumberTestData("1.2e3 ", 1.2e3),
                new NumberTestData("  1.2e3", 1.2e3),
                new NumberTestData("  -1.2e3", -1.2e3),
                new NumberTestData("1.2e3  ", 1.2e3),
                new NumberTestData("   1.2e3   ", 1.2e3)
        );
    }

    public static List<NumberTestData> createDataForIllegalDecStringsWithWhitespace() {
        return Arrays.asList(
                new NumberTestData("parseFloatValue(): Blank String", "   ", 0, 3, 0, 3, AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("+ ", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData(" +", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("- ", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData(" -", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+e ", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData(" +e", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-e ", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData(" -e", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-e123 ", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData(" -e123", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData("-e456 ", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class),
                new NumberTestData(" -e456", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)
        );
    }

    public static List<NumberTestData> createDataForIllegalHexStrings() {
        return Arrays.asList(
                new NumberTestData("0x123pa", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("0x1.2e7", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("0xp89", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class)

        );
    }


}
