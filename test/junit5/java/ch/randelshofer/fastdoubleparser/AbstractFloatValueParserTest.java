/*
 * @(#)AbstractFastXParserTest.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractFloatValueParserTest {

    protected List<TestData> createDataForDecimalLimits() {
        return Arrays.asList(
                new TestData("Double Dec Limit a", Double.toString(Double.MIN_VALUE), Double.MIN_VALUE, (float) Double.MIN_VALUE),
                new TestData("Double Dec Limit b", Double.toString(Double.MAX_VALUE), Double.MAX_VALUE, (float) Double.MAX_VALUE),
                new TestData("Double Dec Limit c", Double.toString(Math.nextUp(0.0)), Math.nextUp(0.0), (float) Math.nextUp(0.0)),
                new TestData("Double Dec Limit d", Double.toString(Math.nextDown(0.0)), Math.nextDown(0.0), (float) Math.nextDown(0.0)),

                new TestData("Float Dec Limit a", Float.toString(Float.MIN_VALUE), 1.4E-45, (float) Float.MIN_VALUE),
                new TestData("Float Dec Limit b", Float.toString(Float.MAX_VALUE), 3.4028235E38, (float) Float.MAX_VALUE),
                new TestData("Float Dec Limit c", Float.toString(Math.nextUp(0.0f)), 1.4E-45, (float) Math.nextUp(0.0f)),
                new TestData("Float Dec Limit d", Float.toString(Math.nextDown(0.0f)), -1.4E-45, (float) Math.nextDown(0.0f))
        );
    }

    protected List<TestData> createDataForHexadecimalLimits() {
        return Arrays.asList(
                new TestData("Double Hex Limit a", Double.toHexString(Double.MIN_VALUE), Double.MIN_VALUE, (float) Double.MIN_VALUE),
                new TestData("Double Hex Limit b", Double.toHexString(Double.MAX_VALUE), Double.MAX_VALUE, (float) Double.MAX_VALUE),
                new TestData("Double Hex Limit c", Double.toHexString(Math.nextUp(0.0)), Math.nextUp(0.0), 0f),
                new TestData("Double Hex Limit d", Double.toHexString(Math.nextDown(0.0)), Math.nextDown(0.0), -0f),

                new TestData("Float Hex Limit", Float.toHexString(Float.MIN_VALUE), Float.MIN_VALUE, (float) Float.MIN_VALUE),
                new TestData("Float Hex Limit", Float.toHexString(Float.MAX_VALUE), Float.MAX_VALUE, (float) Float.MAX_VALUE),
                new TestData("Float Hex Limit", Float.toHexString(Math.nextUp(0.0f)), Math.nextUp(0.0f), (float) Math.nextUp(0.0f)),
                new TestData("Float Hex Limit", Float.toHexString(Math.nextDown(0.0f)), Math.nextDown(0.0f), (float) Math.nextDown(0.0f))
        );
    }

    protected List<TestData> createDataForDecimalClingerInputClasses() {
        return Arrays.asList(
                new TestData("Dec Double: Inside Clinger fast path \"1000000000000000000e-325\")", "1000000000000000000e-325", 1000000000000000000e-325d, 0f),
                new TestData("Dec Double: Inside Clinger fast path (max_clinger_significand, max_clinger_exponent)", "9007199254740991e22", 9007199254740991e22d, 9007199254740991e22f),
                new TestData("Dec Double: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "9007199254740991e23", 9007199254740991e23d, Float.POSITIVE_INFINITY),
                new TestData("Dec Double: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "9007199254740992e22", 9007199254740992e22d, 9007199254740992e22f),
                new TestData("Dec Double: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "1e-22", 1e-22d, 1e-22f),
                new TestData("Dec Double: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "1e-23", 1e-23d, 1e-23f),
                new TestData("Dec Double: Outside Clinger fast path, semi-fast path, 9999999999999999999", "1e23", 1e23d, 1e23f),
                new TestData("Dec Double: Outside Clinger fast path, bail-out in semi-fast path, 1e23", "1e23", 1e23d, 1e23f),
                new TestData("Dec Double: Outside Clinger fast path, mantissa overflows in semi-fast path, 7.2057594037927933e+16", "7.2057594037927933e+16", 7.2057594037927933e+16d, 7.2057594037927933e+16f),
                new TestData("Dec Double: Outside Clinger fast path, bail-out in semi-fast path, 7.3177701707893310e+15", "7.3177701707893310e+15", 7.3177701707893310e+15d, 7.3177701707893310e+15f),

                new TestData("-2.97851206854973E-75", -2.97851206854973E-75, -0f),
                new TestData("3.0286208942000664E-69", 3.0286208942000664E-69, 0f),
                new TestData("3.7587182468424695418288325e-309", 3.7587182468424695418288325e-309, 0f),
                new TestData("10000000000000000000000000000000000000000000e+308", Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        );
    }

    protected List<TestData> createDataForHexadecimalClingerInputClasses() {
        return Arrays.asList(
                new TestData("Hex Double: Inside Clinger fast path (max_clinger_significand)", "0x1fffffffffffffp74", 0x1fffffffffffffp74, 0x1fffffffffffffp74f),
                new TestData("Hex Double: Inside Clinger fast path (max_clinger_significand), negative", "-0x1fffffffffffffp74", -0x1fffffffffffffp74, -0x1fffffffffffffp74f),
                new TestData("Hex Double: Outside Clinger fast path (max_clinger_significand, max_clinger_exponent + 1)", "0x1fffffffffffffp74", 0x1fffffffffffffp74, 0x1fffffffffffffp74f),
                new TestData("Hex Double: Outside Clinger fast path (max_clinger_significand + 1, max_clinger_exponent)", "0x20000000000000p74", 0x20000000000000p74, 0x20000000000000p74f),
                new TestData("Hex Double: Inside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent)", "0x1p-74", 0x1p-74, 0x1p-74f),
                new TestData("Hex Double: Outside Clinger fast path (min_clinger_significand + 1, min_clinger_exponent - 1)", "0x1p-75", 0x1p-75, 0x1p-75f)
        );
    }


}