/*
 * @(#)EightDigitsTestDataFactory.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EightDigitsTestDataFactory {
    private EightDigitsTestDataFactory() {
    }

    @TestFactory
    public static List<NumberTestData> createIllegalEightDecDigitsLiterals() {
        return Arrays.asList(
                new NumberTestData("1234567x"),
                new NumberTestData("x7654321"),
                new NumberTestData("1234Ł678"),
                new NumberTestData("123456/7"),
                new NumberTestData("7/654321"),
                new NumberTestData("12345:67"),
                new NumberTestData("76:54321"),

                new NumberTestData("x12345678xx")
        );
    }

    @TestFactory
    public static List<NumberTestData> createIllegalEightHexDigitsLiterals() {
        return Arrays.asList(
                new NumberTestData("1234567x"),
                new NumberTestData("x7654321"),

                new NumberTestData("x1234567xxx")
        );
    }

    @TestFactory
    public static List<NumberTestData> createLegalEightDecDigitsLiterals() {
        return Arrays.asList(
                new NumberTestData("12345678", 12345678),
                new NumberTestData("87654321", 87654321),
                new NumberTestData("00000000", 0),
                new NumberTestData("99999999", 99999999),

                new NumberTestData("x12345678xx", 12345678, 1, 8)
        );
    }

    @TestFactory
    public static List<NumberTestData> createLegalEightHexDigitsLiterals() {
        return Arrays.asList(
                new NumberTestData("12345678", 0x12345678L),
                new NumberTestData("87654321", 0x87654321L),
                new NumberTestData("00000000", 0L),
                new NumberTestData("ffffffff", 0xffffffffL),
                new NumberTestData("x12345678xx", 0x12345678L, 1, 8)
        );
    }

    @TestFactory
    public static List<NumberTestData> createAllEightHexDigitsLiteralsUtf8() {
        List<NumberTestData> tests = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            final char c = (char) i;
            boolean isValidHexChar = !((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f'));
            byte[] a = new byte[8];
            Arrays.fill(a, (byte) c);
            tests.add(new NumberTestData(String.format("u+%04x", i) + (Character.isISOControl(c) ? "" : " '" + c + "'"), i));
        }
        return tests;
    }

    @TestFactory
    public static List<NumberTestData> createAllEightHexDigitsLiteralsUtf16() {
        List<NumberTestData> tests = new ArrayList<>();
        for (int i = 0; i < 1 << 10; i++) {// we only test the first 1024 chars
            final char c = (char) i;
            boolean isValidHexChar = !((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f'));
            char[] a = new char[8];
            Arrays.fill(a, c);
            tests.add(new NumberTestData(String.format("u+%04x", i) + (Character.isISOControl(c) ? "" : " '" + c + "'"), i));

        }
        return tests;
    }


}
