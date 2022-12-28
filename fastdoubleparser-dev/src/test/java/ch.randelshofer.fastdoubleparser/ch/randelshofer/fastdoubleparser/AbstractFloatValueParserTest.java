/*
 * @(#)AbstractFloatValueParserTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractFloatValueParserTest {

    protected List<NumberTestData> createDataForDoubleDecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Double Dec Limit a", Double.toString(Double.MIN_VALUE), Double.MIN_VALUE),
                new NumberTestData("Double Dec Limit b", Double.toString(Double.MAX_VALUE), Double.MAX_VALUE),
                new NumberTestData("Double Dec Limit c", Double.toString(Math.nextUp(0.0)), Math.nextUp(0.0)),
                new NumberTestData("Double Dec Limit d", Double.toString(Math.nextDown(0.0)), Math.nextDown(0.0))
        );
    }

    protected List<NumberTestData> createDataForFloatDecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Float Dec Limit a", Float.toString(Float.MIN_VALUE), Float.MIN_VALUE),
                new NumberTestData("Float Dec Limit b", Float.toString(Float.MAX_VALUE), Float.MAX_VALUE),
                new NumberTestData("Float Dec Limit c", Float.toString(Math.nextUp(0.0f)), Math.nextUp(0.0f)),
                new NumberTestData("Float Dec Limit d", Float.toString(Math.nextDown(0.0f)), Math.nextDown(0.0f))
        );
    }

    protected List<NumberTestData> createDataForDoubleHexadecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Double Hex Limit a", Double.toHexString(Double.MIN_VALUE), Double.MIN_VALUE),
                new NumberTestData("Double Hex Limit b", Double.toHexString(Double.MAX_VALUE), Double.MAX_VALUE),
                new NumberTestData("Double Hex Limit c", Double.toHexString(Math.nextUp(0.0)), Math.nextUp(0.0)),
                new NumberTestData("Double Hex Limit d", Double.toHexString(Math.nextDown(0.0)), Math.nextDown(0.0))
        );
    }

    protected List<NumberTestData> createDataForFloatHexadecimalLimits() {
        return Arrays.asList(
                new NumberTestData("Float Hex Limit", Float.toHexString(Float.MIN_VALUE), Float.MIN_VALUE),
                new NumberTestData("Float Hex Limit", Float.toHexString(Float.MAX_VALUE), Float.MAX_VALUE),
                new NumberTestData("Float Hex Limit", Float.toHexString(Math.nextUp(0.0f)), Math.nextUp(0.0f)),
                new NumberTestData("Float Hex Limit", Float.toHexString(Math.nextDown(0.0f)), Math.nextDown(0.0f))
        );
    }

    protected List<NumberTestData> createDataForDoubleDecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Dec Double: Inside Clinger fast path \"1000000000000000000e-325\")", "1000000000000000000e-325", 1000000000000000000e-325d),
                new NumberTestData("Dec Double: Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", "9007199254740991e22", 9007199254740991e22d),
                new NumberTestData("Dec Double: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "9007199254740991e23", 9007199254740991e23d),
                new NumberTestData("Dec Double: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "9007199254740992e22", 9007199254740992e22d),
                new NumberTestData("Dec Double: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "1e-22", 1e-22d),
                new NumberTestData("Dec Double: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "1e-23", 1e-23d),
                new NumberTestData("Dec Double: Outside Clinger fast path, bail-out in semi-fast path, 1e23", "1e23", 1e23d),
                new NumberTestData("Dec Double: Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16", "7.2057594037927933e+16", 7.2057594037927933e+16d),
                new NumberTestData("Dec Double: Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15", "7.3177701707893310e+15", 7.3177701707893310e+15d)

                //   new NumberTestData("-2.97851206854973E-75", -2.97851206854973E-75),
                //   new NumberTestData("3.0286208942000664E-69", 3.0286208942000664E-69),
                //   new NumberTestData("3.7587182468424695418288325e-309", 3.7587182468424695418288325e-309),
                //   new NumberTestData("10000000000000000000000000000000000000000000e+308", Double.POSITIVE_INFINITY)
        );
    }

    protected List<NumberTestData> createDataForFloatDecimalClingerInputClasses() {
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

    protected List<NumberTestData> createDataForDoubleHexadecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Hex Double: Inside Clinger fast path (max_clinger_significand)", "0x1fffffffffffffp74", 0x1fffffffffffffp74),
                new NumberTestData("Hex Double: Inside Clinger fast path (max_clinger_significand), negative", "-0x1fffffffffffffp74", -0x1fffffffffffffp74),
                new NumberTestData("Hex Double: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "0x1fffffffffffffp75", 0x1fffffffffffffp75),
                new NumberTestData("Hex Double: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "0x20000000000000p74", 0x20000000000000p74),
                new NumberTestData("Hex Double: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "0x1p-74", 0x1p-74),
                new NumberTestData("Hex Double: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "0x1p-75", 0x1p-75)
        );
    }

    protected List<NumberTestData> createDataForFloatHexadecimalClingerInputClasses() {
        return Arrays.asList(
                new NumberTestData("Hex Float: Inside Clinger fast path (max_clinger_significand)", "0x3FFFFFFFFFp74", 0x3FFFFFFFFFp74f),
                new NumberTestData("Hex Float: Inside Clinger fast path (max_clinger_significand), negative", "-0x3FFFFFFFFFp74", -0x3FFFFFFFFFp74f),
                new NumberTestData("Hex Float: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "0x3FFFFFFFFFp75", 0x3FFFFFFFFFp75f),
                new NumberTestData("Hex Float: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "0x4000000000p74", 0x4000000000p74f),
                new NumberTestData("Hex Float: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "0x1p-74", 0x1p-74f),
                new NumberTestData("Hex Float: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "0x1p-75", 0x1p-75f)
        );
    }

    protected List<NumberTestData> createDataForSignificandDigitsInputClasses() {
        return Arrays.asList(
                // In the worst case, we must consider up to 768 digits in the significand
                new NumberTestData("Round Down due to value in 768-th digit",
                        "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984374E-308",
                        2.225073858507202E-308),
                new NumberTestData("Round Up due to value in 768-th digit ",
                        "2.22507385850720212418870147920222032907240528279439037814303133837435107319244194686754406432563881851382188218502438069999947733013005649884107791928741341929297200970481951993067993290969042784064731682041565926728632933630474670123316852983422152744517260835859654566319282835244787787799894310779783833699159288594555213714181128458251145584319223079897504395086859412457230891738946169368372321191373658977977723286698840356390251044443035457396733706583981055420456693824658413747607155981176573877626747665912387199931904006317334709003012790188175203447190250028061277777916798391090578584006464715943810511489154282775041174682194133952466682503431306181587829379004205392375072083366693241580002758391118854188641513168478436313080237596295773983001708984375E-308",
                        2.2250738585072024E-308)
        );
    }

    protected List<NumberTestData> createDataWithVeryLongInputStrings() {
        return Arrays.asList(
                new NumberTestData("too many input characters", new VirtualCharSequence('1', Integer.MAX_VALUE - 3), AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH, IllegalArgumentException.class),
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