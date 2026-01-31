/*
 * @(#)FloatValueTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public abstract class GenericDoubleDataSource {
    public static List<NumberTestData> createDataForLegalDoubleDecStrings() {
        return Arrays.asList(
                new NumberTestData("0", 0),
                new NumberTestData("1", 1),
                new NumberTestData("1.2", 1.2),
                new NumberTestData("1.2e3", 1.2e3),
                new NumberTestData("1.2E3", 1.2e3),
                new NumberTestData("-1", -1),
                new NumberTestData("-1.2", -1.2),
                new NumberTestData("-1.2e3", -1.2e3),
                new NumberTestData("-1.2E3", -1.2e3),
                new NumberTestData("1.2e-3", 1.2e-3),
                new NumberTestData("1.2E-3", 1.2e-3),
                new NumberTestData("1.2e+3", 1.2e+3),
                new NumberTestData("1.2E+3", 1.2e+3),
                new NumberTestData("-1.2e+3", -1.2e3),
                new NumberTestData("-1.2E-3", -1.2e-3),
                new NumberTestData("1234567890", 1234567890d),
                new NumberTestData("-30000000000000000000000000", -3e25)
        );
    }

    public static List<NumberTestData> createDataForDoubleDecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Double Dec MIN_VALUE", Double.toString(Double.MIN_VALUE), Double.MIN_VALUE),
                new NumberTestData("Double Dec MIN_NORMAL", Double.toString(Double.MIN_NORMAL), Double.MIN_NORMAL),
                new NumberTestData("Double Dec MAX_VALUE", Double.toString(Double.MAX_VALUE), Double.MAX_VALUE),
                new NumberTestData("Double Dec nextUp(0)", Double.toString(Math.nextUp(0.0)), Math.nextUp(0.0)),
                new NumberTestData("Double Dec nextDown(0)", Double.toString(Math.nextDown(0.0)), Math.nextDown(0.0))
        );
    }

    public static List<NumberTestData> createDataForDoubleHexadecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Double Hex MIN_VALUE", Double.toHexString(Double.MIN_VALUE), Double.MIN_VALUE),
                new NumberTestData("Double Hex MIN_NORMAL", Double.toHexString(Double.MIN_NORMAL), Double.MIN_NORMAL),
                new NumberTestData("Double Hex MAX_VALUE", Double.toHexString(Double.MAX_VALUE), Double.MAX_VALUE),
                new NumberTestData("Double Hex nextUp(0)", Double.toHexString(Math.nextUp(0.0)), Math.nextUp(0.0)),
                new NumberTestData("Double Hex nextDown(0)", Double.toHexString(Math.nextDown(0.0)), Math.nextDown(0.0))
        );
    }

    public static List<NumberTestData> createDataForDoubleDecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Dec Double: Inside Clinger fast path \"1000000000000000000e-325\")", "1000000000000000000e-325", 1000000000000000000e-325d),
                new NumberTestData("Dec Double: Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", "9007199254740991e22", 9007199254740991e22d),
                new NumberTestData("Dec Double: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "9007199254740991e23", 9007199254740991e23d),
                new NumberTestData("Dec Double: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "9007199254740992e22", 9007199254740992e22d),
                new NumberTestData("Dec Double: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "1e-22", 1e-22d),
                new NumberTestData("Dec Double: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "1e-23", 1e-23d),
                new NumberTestData("Dec Double: Outside Clinger fast path, bail-out in semi-fast path, 1e23", "1e23", 1e23d),
                new NumberTestData("Dec Double: Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16", "7.2057594037927933e+16", 7.2057594037927933e+16d),
                new NumberTestData("Dec Double: Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15", "7.3177701707893310e+15", 7.3177701707893310e+15d),
                new NumberTestData("Dec Double: Slow path with leading zero digits, 0.0032714205467505586", "0.0032714205467505586", 0.0032714205467505586)
        );
    }

    public static List<NumberTestData> createDataForDoubleHexadecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Hex Double: Inside Clinger fast path (max_clinger_significand)", "0x1fffffffffffffp74", 0x1fffffffffffffp74),
                new NumberTestData("Hex Double: Inside Clinger fast path (max_clinger_significand), negative", "-0x1fffffffffffffp74", -0x1fffffffffffffp74),
                new NumberTestData("Hex Double: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "0x1fffffffffffffp75", 0x1fffffffffffffp75),
                new NumberTestData("Hex Double: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "0x20000000000000p74", 0x20000000000000p74),
                new NumberTestData("Hex Double: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "0x1p-74", 0x1p-74),
                new NumberTestData("Hex Double: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "0x1p-75", 0x1p-75)
        );
    }

    public static List<NumberTestData> createDataForDoubleSignificandDigitsInputClasses() {
        return Arrays.asList(
                // In the worst case, we must consider up to 768 digits in the significand
                new NumberTestData("Round Down due to value in 768-th digit E-308",
                        "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984374E-308",
                        2.225073858507202E-308),
                new NumberTestData("Round Up due to value in 768-th digit E-308",
                        "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308",
                        2.2250738585072024E-308),
                new NumberTestData("Round Up due to value in 768-th digit E-324",
                        new BigDecimal(Math.nextUp(Double.MIN_VALUE)).subtract(new BigDecimal(Double.MIN_VALUE)).divide(BigDecimal.valueOf(2)).add(new BigDecimal(Double.MIN_VALUE)).toString(),
                        9.9E-324
                ),
                new NumberTestData("Round Down due to value in 768-th digit E-324",
                        new BigDecimal(Math.nextUp(Double.MIN_VALUE)).subtract(new BigDecimal(Double.MIN_VALUE)).divide(BigDecimal.valueOf(2)).add(new BigDecimal(Double.MIN_VALUE))
                                .subtract(new BigDecimal(BigInteger.valueOf(1), 1075)).toString(),
                        4.9E-324
                )
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