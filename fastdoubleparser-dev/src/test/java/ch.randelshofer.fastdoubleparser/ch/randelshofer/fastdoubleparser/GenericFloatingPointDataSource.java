/*
 * @(#)GenericFloatingPointDataSource.java
 * Copyright © 2026 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;
import java.util.List;

public class GenericFloatingPointDataSource {

    /**
     * All these strings must throw a {@link NumberFormatException}.
     */
    public static List<NumberTestData> createDataForIllegalStrings() {
        return Arrays.asList(
                new NumberTestData("empty", "", AbstractNumberParser.EMPTY_ERROR, NumberFormatException.class),
                new NumberTestData("+", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+e", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-e", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("+e123", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-e456", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("78 e9", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("7 8", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("7 8 9", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("-01 e23", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("- 1", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData("-0 .5", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("-0. 5", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("-0.5 e", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("-0.5e 3", AbstractNumberParser.ILLEGAL_EXPONENT_ERROR, NumberFormatException.class),
                new NumberTestData("45\ne6", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("d", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class),
                new NumberTestData(".f", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("7_8e90", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("12e3_4", AbstractNumberParser.ILLEGAL_EXPONENT_ERROR, NumberFormatException.class),
                new NumberTestData("1..2", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("1.2ee3", AbstractNumberParser.ILLEGAL_EXPONENT_ERROR, NumberFormatException.class),
                new NumberTestData("00x5.6p7", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("89p0", AbstractNumberParser.ILLEGAL_SIGNIFICAND_ERROR, NumberFormatException.class),
                new NumberTestData("cafebabe.1p2", AbstractNumberParser.ILLEGAL_FLOATING_POINT_VALUE, NumberFormatException.class)
        );
    }
}
