/*
 * @(#)FloatValueTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public abstract class GenericFloatDataSource {
    public static List<NumberTestData> createDataForLegalFloatDecStrings() {
        return Arrays.asList(
                new NumberTestData("-1", -1f),
                new NumberTestData("-1.2", -1.2f),
                new NumberTestData("-1.2E-3", -1.2e-3f),
                new NumberTestData("-1.2E3", -1.2e3f),
                new NumberTestData("-1.2e+3", -1.2e3f),
                new NumberTestData("-1.2e3", -1.2e3f),
                new NumberTestData("-30000000000000000000000000", -3e25f),
                new NumberTestData("0", 0f),
                new NumberTestData("1", 1f),
                new NumberTestData("1.2", 1.2f),
                new NumberTestData("1.2E+3", 1.2e+3f),
                new NumberTestData("1.2E-3", 1.2e-3f),
                new NumberTestData("1.2E3", 1.2e3f),
                new NumberTestData("1.2e+3", 1.2e+3f),
                new NumberTestData("1.2e-3", 1.2e-3f),
                new NumberTestData("1.2e3", 1.2e3f),
                new NumberTestData("1234567890", 1234567890f)
        );
    }


    public static List<NumberTestData> createDataForFloatDecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Float Dec MIN_VALUE", Float.toString(Float.MIN_VALUE), Float.MIN_VALUE),
                new NumberTestData("Float Dec MIN_NORMAL", Float.toString(Float.MIN_NORMAL), Float.MIN_NORMAL),
                new NumberTestData("Float Dec MAX_VALUE", Float.toString(Float.MAX_VALUE), Float.MAX_VALUE),
                new NumberTestData("Float Dec nextUp(0)", Float.toString(Math.nextUp(0.0f)), Math.nextUp(0.0f)),
                new NumberTestData("Float Dec nextDown(0)", Float.toString(Math.nextDown(0.0f)), Math.nextDown(0.0f))
        );
    }


    public static List<NumberTestData> createDataForFloatHexadecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Float Hex MIN_VALUE", Float.toHexString(Float.MIN_VALUE), Float.MIN_VALUE),
                new NumberTestData("Float Hex MIN_NORMAL", Float.toHexString(Float.MIN_NORMAL), Float.MIN_NORMAL),
                new NumberTestData("Float Hex MAX_VALUE", Float.toHexString(Float.MAX_VALUE), Float.MAX_VALUE),
                new NumberTestData("Float Hex nextUp(0)", Float.toHexString(Math.nextUp(0.0f)), Math.nextUp(0.0f)),
                new NumberTestData("Float Hex nextDown(0)", Float.toHexString(Math.nextDown(0.0f)), Math.nextDown(0.0f))
        );
    }


    public static List<NumberTestData> createDataForFloatDecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Dec Float: Inside Clinger fast path \"1000000000000000000e-45f\")", "1000000000000000000e-45f", 1000000000000000000e-45f),
                new NumberTestData("Dec Float: Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", "274877906943e22", 274877906943e22f),
                new NumberTestData("Dec Float: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "274877906943e23", 274877906943e23f),
                new NumberTestData("Dec Float: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "274877906943e22", 274877906943e22f),
                new NumberTestData("Dec Float: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "1e-10", 1e-10f),
                new NumberTestData("Dec Float: Inside Clinger fast path (min_clinger_significand + 1, max_clinger_exponent)", "1e10", 1e10f),
                new NumberTestData("Dec Float: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "1e-11", 1e-11f),
                new NumberTestData("Dec Float: Outside Clinger fast path (min_clinger_significand + 1, max_clinger_exponent + 1)", "1e-12", 1e-12f),
                new NumberTestData("Dec Float: Outside Clinger fast path, bail-out in semi-fast path, 374622.75", "374622.75", 374622.75f),
                new NumberTestData("Dec Float: Outside Clinger fast path, bail-out in semi-fast path at 'we have to round to even', 0.056640625", "0.056640625", 0.056640625f),
                new NumberTestData("Dec Float: Outside Clinger fast path, bail-out in semi-fast path at 'we have to check that exponent is in range', -1.231457E-39", "-1.231457E-39", -1.231457E-39f),
                new NumberTestData("Dec Float: Outside Clinger fast path, mantissa overflows in semi-fast path, 1.4757395E20", "1.4757395E20", 1.4757395E20f)
        );
    }


    public static List<NumberTestData> createDataForFloatHexadecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Hex Float: Inside Clinger fast path (max_clinger_significand)", "0x3FFFFFFFFFp74", 0x3FFFFFFFFFp74f),
                new NumberTestData("Hex Float: Inside Clinger fast path (max_clinger_significand), negative", "-0x3FFFFFFFFFp74", -0x3FFFFFFFFFp74f),
                new NumberTestData("Hex Float: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "0x3FFFFFFFFFp75", 0x3FFFFFFFFFp75f),
                new NumberTestData("Hex Float: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "0x4000000000p74", 0x4000000000p74f),
                new NumberTestData("Hex Float: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "0x1p-74", 0x1p-74f),
                new NumberTestData("Hex Float: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "0x1p-75", 0x1p-75f)
        );
    }


    public static List<NumberTestData> createDataForFloatSignificandDigitsInputClasses() {
        return Arrays.asList(
                // In the worst case, we must consider up to 112 digits in the significand
                new NumberTestData("Round Up due to value in 112-th digit E-45f",
                        new BigDecimal(Math.nextUp(Float.MIN_VALUE)).subtract(new BigDecimal(Float.MIN_VALUE)).divide(BigDecimal.valueOf(2)).add(new BigDecimal(Float.MIN_VALUE)).toString(),
                        2.8e-45f),
                new NumberTestData("Round Down due to value in 112-th digit E-45f",
                        new BigDecimal(Math.nextUp(Float.MIN_VALUE)).subtract(new BigDecimal(Float.MIN_VALUE)).divide(BigDecimal.valueOf(2)).add(new BigDecimal(Float.MIN_VALUE))
                                .subtract(new BigDecimal(BigInteger.valueOf(1), 150)).toString(),
                        1.4e-45f)
        );
    }


    public static List<NumberTestData> createDataWithVeryLongInputStrings() {
        return Arrays.asList(
                new NumberTestData("too many input characters", new VirtualCharSequence('1', Integer.MAX_VALUE - 4), 0, Integer.MAX_VALUE - 3, 0, Integer.MAX_VALUE - 3, AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
                new NumberTestData("significand with maximal number of zero digits in integer part", new VirtualCharSequence('0', Integer.MAX_VALUE - 4), 0d),
                new NumberTestData("significand with maximal number of zero digits in fraction part", new VirtualCharSequence(".", '0', Integer.MAX_VALUE - 4), 0d),
                new NumberTestData("significand with maximal number of zero digits in significand and decimal point at char 1024", new VirtualCharSequence("", 1024, ".", "", '0', Integer.MAX_VALUE - 4), 0d)

                // FIXME - Fix the float parser and then re-enable these tests
                // ,
                //new NumberTestData("'9' ** MAX_VALUE - 4 -- fails with Float.parseFloat(String)", new VirtualCharSequence('9', Integer.MAX_VALUE - 4), Double.POSITIVE_INFINITY),
                //new NumberTestData("'9' ** (1<<30) -- fails with Float.parseFloat(String)", new VirtualCharSequence('9', 1<<30), Double.POSITIVE_INFINITY)
        );
    }
}