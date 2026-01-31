/*
 * @(#)JavaGenericFloatingPointParserDataSource.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;
import java.util.List;

public class JsonGenericFloatingPointDataSource {
    public static List<NumberTestData> createTestDataForInfinity() {
        return Arrays.asList(
                new NumberTestData("Infinity"),
                new NumberTestData("+Infinity"),
                new NumberTestData("-Infinity"),
                new NumberTestData("Infinit"),
                new NumberTestData("INfinity"),
                new NumberTestData("InFinity"),
                new NumberTestData("InfInity"),
                new NumberTestData("InfiNity"),
                new NumberTestData("InfinIty"),
                new NumberTestData("InfiniTy"),
                new NumberTestData("InfinitY"),
                new NumberTestData("+ Infinity"),
                new NumberTestData("+.Infinity"),
                new NumberTestData("eInfinity"),
                new NumberTestData("5Infinity"),
                new NumberTestData("0Infinity"),
                new NumberTestData(".Infinity"),
                new NumberTestData("+Infinityf"),
                new NumberTestData("-InfinityF"),
                new NumberTestData("+Infinityd"),
                new NumberTestData("+-InfinityD"),
                new NumberTestData("+InfinityNaN"),
                new NumberTestData("infinity")
        );
    }

    public static List<NumberTestData> createTestDataForNaN() {
        return Arrays.asList(
                new NumberTestData("NaN"),
                new NumberTestData("+NaN"),
                new NumberTestData("-NaN"),
                new NumberTestData("NaNf"),
                new NumberTestData("+NaNd"),
                new NumberTestData("-NaNF"),
                new NumberTestData("+-NaND"),
                new NumberTestData("NaNInfinity"),
                new NumberTestData("nan"),
                new NumberTestData("Non"),
                new NumberTestData("Nan"),
                new NumberTestData("NaNNy")
        );
    }


    public static List<NumberTestData> createDataForIllegalDecStringsWithWhitespace() {
        return Arrays.asList(
                new NumberTestData("Significand with leading whitespace", "   3"),
                new NumberTestData("Significand with trailing whitespace", "3   "),
                new NumberTestData(" 1.2e3"),
                new NumberTestData("1.2e3 "),
                new NumberTestData("  1.2e3"),
                new NumberTestData("  -1.2e3"),
                new NumberTestData("1.2e3  "),
                new NumberTestData("   1.2e3   3")
        );
    }


}
