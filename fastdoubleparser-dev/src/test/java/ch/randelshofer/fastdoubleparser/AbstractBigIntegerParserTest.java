/*
 * @(#)AbstractBigIntegerParserTest.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.io.BufferedWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static ch.randelshofer.fastdoubleparser.Strings.repeat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractBigIntegerParserTest {
    private boolean longRunningTests = !"false".equals(System.getProperty("enableLongRunningTests"));

    public static BigInteger createBigIntegerMaxValue() {
        byte[] bytes = new byte[Integer.MAX_VALUE / 8 + 1];
        Arrays.fill(bytes, (byte) -1);
        bytes[0] = (byte) 0x7f;
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger;
    }

    /**
     * This takes about 2h 17m.
     */
    public static void writeBigIntegerMaxValueFile() throws Exception {
        Path p = Paths.get("fastdoubleparserdemo/data/BigIntegerMaxValue.txt");
        if (!Files.exists(p)) {
            BigInteger b = createBigIntegerMaxValue();
            try (BufferedWriter w = Files.newBufferedWriter(p)) {
                String s = b.toString();
                w.write(s);
                w.newLine();
            }
        }
    }

    protected void test(NumberTestDataSupplier s, Function<NumberTestData, BigInteger> f) {
        NumberTestData d = s.supplier().get();
        BigInteger expectedValue = (BigInteger) d.expectedValue();
        BigInteger actual = null;
        try {
            actual = f.apply(d);
        } catch (IllegalArgumentException e) {
            // if (!Objects.equals(d.expectedErrorMessage(), e.getMessage())) {
            //     e.printStackTrace();
            //     assertEquals(d.expectedErrorMessage(), e.getMessage());
            // }
            assertEquals(d.expectedThrowableClass(), e.getClass());
        }
        if (expectedValue != null) {
            assertEquals(0, actual == null ? -1 : expectedValue.compareTo(actual),
                    "expected:" + expectedValue.bitLength() + " <> actual:" + actual.bitLength());
            assertEquals(expectedValue, actual);
        } else {
            // Do not use assertNull(actual) here, because it will call toString
            // on a potentially very large BigInteger!
            assertTrue(actual == null);
        }
    }

    protected List<NumberTestDataSupplier> createDataForLegalRadixStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("1, radix 2", () -> new NumberTestData("1, radix 2", "1", 2, new BigInteger("1", 2))),
                new NumberTestDataSupplier("2, radix 3", () -> new NumberTestData("2, radix 3", "2", 3, new BigInteger("2", 3))),
                new NumberTestDataSupplier("3, radix 4", () -> new NumberTestData("3, radix 4", "3", 4, new BigInteger("3", 4))),
                new NumberTestDataSupplier("4, radix 5", () -> new NumberTestData("4, radix 5", "4", 5, new BigInteger("4", 5))),
                new NumberTestDataSupplier("5, radix 6", () -> new NumberTestData("5, radix 6", "5", 6, new BigInteger("5", 6))),
                new NumberTestDataSupplier("6, radix 7", () -> new NumberTestData("6, radix 7", "6", 7, new BigInteger("6", 7))),
                new NumberTestDataSupplier("7, radix 8", () -> new NumberTestData("7, radix 8", "7", 8, new BigInteger("7", 8))),
                new NumberTestDataSupplier("8, radix 9", () -> new NumberTestData("8, radix 9", "8", 9, new BigInteger("8", 9))),
                new NumberTestDataSupplier("9, radix 10", () -> new NumberTestData("9, radix 10", "9", 10, new BigInteger("9", 10))),
                new NumberTestDataSupplier("a, radix 11", () -> new NumberTestData("a, radix 11", "a", 11, new BigInteger("a", 11))),
                new NumberTestDataSupplier("b, radix 12", () -> new NumberTestData("b, radix 12", "b", 12, new BigInteger("b", 12))),
                new NumberTestDataSupplier("c, radix 13", () -> new NumberTestData("c, radix 13", "c", 13, new BigInteger("c", 13))),
                new NumberTestDataSupplier("d, radix 14", () -> new NumberTestData("d, radix 14", "d", 14, new BigInteger("d", 14))),
                new NumberTestDataSupplier("e, radix 15", () -> new NumberTestData("e, radix 15", "e", 15, new BigInteger("e", 15))),
                new NumberTestDataSupplier("f, radix 16", () -> new NumberTestData("f, radix 16", "f", 16, new BigInteger("f", 16))),
                new NumberTestDataSupplier("g, radix 17", () -> new NumberTestData("g, radix 17", "g", 17, new BigInteger("g", 17))),
                new NumberTestDataSupplier("h, radix 18", () -> new NumberTestData("h, radix 18", "h", 18, new BigInteger("h", 18))),
                new NumberTestDataSupplier("i, radix 19", () -> new NumberTestData("i, radix 19", "i", 19, new BigInteger("i", 19))),
                new NumberTestDataSupplier("j, radix 20", () -> new NumberTestData("j, radix 20", "j", 20, new BigInteger("j", 20))),
                new NumberTestDataSupplier("k, radix 21", () -> new NumberTestData("k, radix 21", "k", 21, new BigInteger("k", 21))),
                new NumberTestDataSupplier("l, radix 22", () -> new NumberTestData("l, radix 22", "l", 22, new BigInteger("l", 22))),
                new NumberTestDataSupplier("m, radix 23", () -> new NumberTestData("m, radix 23", "m", 23, new BigInteger("m", 23))),
                new NumberTestDataSupplier("n, radix 24", () -> new NumberTestData("n, radix 24", "n", 24, new BigInteger("n", 24))),
                new NumberTestDataSupplier("o, radix 25", () -> new NumberTestData("o, radix 25", "o", 25, new BigInteger("o", 25))),
                new NumberTestDataSupplier("p, radix 26", () -> new NumberTestData("p, radix 26", "p", 26, new BigInteger("p", 26))),
                new NumberTestDataSupplier("q, radix 27", () -> new NumberTestData("q, radix 27", "q", 27, new BigInteger("q", 27))),
                new NumberTestDataSupplier("r, radix 28", () -> new NumberTestData("r, radix 28", "r", 28, new BigInteger("r", 28))),
                new NumberTestDataSupplier("s, radix 29", () -> new NumberTestData("s, radix 29", "s", 29, new BigInteger("s", 29))),
                new NumberTestDataSupplier("t, radix 30", () -> new NumberTestData("t, radix 30", "t", 30, new BigInteger("t", 30))),
                new NumberTestDataSupplier("u, radix 31", () -> new NumberTestData("u, radix 31", "u", 31, new BigInteger("u", 31))),
                new NumberTestDataSupplier("v, radix 32", () -> new NumberTestData("v, radix 32", "v", 32, new BigInteger("v", 32))),
                new NumberTestDataSupplier("w, radix 33", () -> new NumberTestData("w, radix 33", "w", 33, new BigInteger("w", 33))),
                new NumberTestDataSupplier("x, radix 34", () -> new NumberTestData("x, radix 34", "x", 34, new BigInteger("x", 34))),
                new NumberTestDataSupplier("y, radix 35", () -> new NumberTestData("y, radix 35", "y", 35, new BigInteger("y", 35))),
                new NumberTestDataSupplier("z, radix 36", () -> new NumberTestData("z, radix 36", "z", 36, new BigInteger("z", 36)))
        );
    }

    protected List<NumberTestDataSupplier> createDataForIllegalRadixStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("1, radix 1", () -> new NumberTestData("1, radix 1", "1", 1, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("2, radix 2", () -> new NumberTestData("2, radix 2", "2", 2, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("3, radix 3", () -> new NumberTestData("3, radix 3", "3", 3, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("4, radix 4", () -> new NumberTestData("4, radix 4", "4", 4, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("5, radix 5", () -> new NumberTestData("5, radix 5", "5", 5, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("6, radix 6", () -> new NumberTestData("6, radix 6", "6", 6, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("7, radix 7", () -> new NumberTestData("7, radix 7", "7", 7, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("8, radix 8", () -> new NumberTestData("8, radix 8", "8", 8, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("9, radix 9", () -> new NumberTestData("9, radix 9", "9", 9, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("a, radix 10", () -> new NumberTestData("a, radix 10", "a", 10, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("b, radix 11", () -> new NumberTestData("b, radix 11", "b", 11, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("c, radix 12", () -> new NumberTestData("c, radix 12", "c", 12, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("d, radix 13", () -> new NumberTestData("d, radix 13", "d", 13, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("e, radix 14", () -> new NumberTestData("e, radix 14", "e", 14, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("f, radix 15", () -> new NumberTestData("f, radix 15", "f", 15, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("g, radix 16", () -> new NumberTestData("g, radix 16", "g", 16, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("h, radix 17", () -> new NumberTestData("h, radix 17", "h", 17, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("i, radix 18", () -> new NumberTestData("i, radix 18", "i", 18, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("j, radix 19", () -> new NumberTestData("j, radix 19", "j", 19, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("k, radix 20", () -> new NumberTestData("k, radix 20", "k", 20, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("l, radix 21", () -> new NumberTestData("l, radix 21", "l", 21, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("m, radix 22", () -> new NumberTestData("m, radix 22", "m", 22, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("n, radix 23", () -> new NumberTestData("n, radix 23", "n", 23, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("o, radix 24", () -> new NumberTestData("o, radix 24", "o", 24, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("p, radix 25", () -> new NumberTestData("p, radix 25", "p", 25, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("q, radix 26", () -> new NumberTestData("q, radix 26", "q", 26, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("r, radix 27", () -> new NumberTestData("r, radix 27", "r", 27, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("s, radix 28", () -> new NumberTestData("s, radix 28", "s", 28, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("t, radix 29", () -> new NumberTestData("t, radix 29", "t", 29, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("u, radix 30", () -> new NumberTestData("u, radix 30", "u", 30, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("v, radix 31", () -> new NumberTestData("v, radix 31", "v", 31, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("w, radix 32", () -> new NumberTestData("w, radix 32", "w", 32, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("x, radix 33", () -> new NumberTestData("x, radix 33", "x", 33, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("y, radix 34", () -> new NumberTestData("y, radix 34", "y", 34, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("z, radix 35", () -> new NumberTestData("z, radix 35", "z", 35, "*", NumberFormatException.class)),
                new NumberTestDataSupplier("_, radix 35", () -> new NumberTestData("_, radix 35", "_", 36, "*", NumberFormatException.class))
        );
    }

    protected List<NumberTestDataSupplier> createDataForLegalHexStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("0", () -> new NumberTestData("0", 16, BigInteger.ZERO)),
                new NumberTestDataSupplier("1", () -> new NumberTestData("1", 16, BigInteger.ONE)),
                new NumberTestDataSupplier("a", () -> new NumberTestData("a", 16, BigInteger.TEN)),

                new NumberTestDataSupplier("00", () -> new NumberTestData("00", 16, BigInteger.ZERO)),
                new NumberTestDataSupplier("01", () -> new NumberTestData("01", 16, BigInteger.ONE)),
                new NumberTestDataSupplier("00000000", () -> new NumberTestData("00000000", 16, BigInteger.ZERO)),
                new NumberTestDataSupplier("00000001", () -> new NumberTestData("00000001", 16, BigInteger.ONE)),

                new NumberTestDataSupplier("1", () -> new NumberTestData("1", 16, new BigInteger("1", 16))),
                new NumberTestDataSupplier("12", () -> new NumberTestData("12", 16, new BigInteger("12", 16))),
                new NumberTestDataSupplier("123", () -> new NumberTestData("123", 16, new BigInteger("123", 16))),
                new NumberTestDataSupplier("1234", () -> new NumberTestData("1234", 16, new BigInteger("1234", 16))),
                new NumberTestDataSupplier("12345", () -> new NumberTestData("12345", 16, new BigInteger("12345", 16))),
                new NumberTestDataSupplier("123456", () -> new NumberTestData("123456", 16, new BigInteger("123456", 16))),
                new NumberTestDataSupplier("1234567", () -> new NumberTestData("1234567", 16, new BigInteger("1234567", 16))),
                new NumberTestDataSupplier("12345678", () -> new NumberTestData("12345678", 16, new BigInteger("12345678", 16))),

                new NumberTestDataSupplier("-0", () -> new NumberTestData("-0", 16, BigInteger.ZERO.negate())),
                new NumberTestDataSupplier("-1", () -> new NumberTestData("-1", 16, BigInteger.ONE.negate())),
                new NumberTestDataSupplier("+1", () -> new NumberTestData("+1", 16, BigInteger.ONE)),
                new NumberTestDataSupplier("-a", () -> new NumberTestData("-a", 16, BigInteger.TEN.negate())),
                new NumberTestDataSupplier("ff", () -> new NumberTestData("ff", 16, new BigInteger("ff", 16))),
                new NumberTestDataSupplier("-ff", () -> new NumberTestData("-ff", 16, new BigInteger("-ff", 16))),
                new NumberTestDataSupplier("-12345678", () -> new NumberTestData("-12345678", 16, new BigInteger("-12345678", 16))),
                new NumberTestDataSupplier("+12345678", () -> new NumberTestData("+12345678", 16, new BigInteger("+12345678", 16)))
        );
    }

    protected List<NumberTestDataSupplier> createDataForIllegalStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("AAAA", () -> new NumberTestData("AAAA", "AAAA", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("A**1500", () -> new NumberTestData("A**1500", new VirtualCharSequence('A', 1500), AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class)),
                new NumberTestDataSupplier("0x1", () -> new NumberTestData("0x1", "0x1", AbstractNumberParser.SYNTAX_ERROR, NumberFormatException.class))
        );
    }


    protected List<NumberTestDataSupplier> createDataForLegalDecStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("0", () -> new NumberTestData("0", BigInteger.ZERO)),
                new NumberTestDataSupplier("1", () -> new NumberTestData("1", BigInteger.ONE)),
                new NumberTestDataSupplier("10", () -> new NumberTestData("10", BigInteger.TEN)),

                new NumberTestDataSupplier("00", () -> new NumberTestData("00", BigInteger.ZERO)),
                new NumberTestDataSupplier("01", () -> new NumberTestData("01", BigInteger.ONE)),
                new NumberTestDataSupplier("00000000", () -> new NumberTestData("00000000", BigInteger.ZERO)),
                new NumberTestDataSupplier("00000001", () -> new NumberTestData("00000001", BigInteger.ONE)),

                new NumberTestDataSupplier("1", () -> new NumberTestData("1", new BigInteger("1"))),
                new NumberTestDataSupplier("12", () -> new NumberTestData("12", new BigInteger("12"))),
                new NumberTestDataSupplier("123", () -> new NumberTestData("123", new BigInteger("123"))),
                new NumberTestDataSupplier("1234", () -> new NumberTestData("1234", new BigInteger("1234"))),
                new NumberTestDataSupplier("12345", () -> new NumberTestData("12345", new BigInteger("12345"))),
                new NumberTestDataSupplier("123456", () -> new NumberTestData("123456", new BigInteger("123456"))),
                new NumberTestDataSupplier("1234567", () -> new NumberTestData("1234567", new BigInteger("1234567"))),
                new NumberTestDataSupplier("12345678", () -> new NumberTestData("12345678", new BigInteger("12345678"))),

                new NumberTestDataSupplier("123456789012345678901234567890", () -> new NumberTestData("123456789012345678901234567890",
                        new BigInteger("123456789012345678901234567890"))),

                new NumberTestDataSupplier("-0", () -> new NumberTestData("-0", BigInteger.ZERO.negate())),
                new NumberTestDataSupplier("-1", () -> new NumberTestData("-1", BigInteger.ONE.negate())),
                new NumberTestDataSupplier("-10", () -> new NumberTestData("-10", BigInteger.TEN.negate())),
                new NumberTestDataSupplier("+10", () -> new NumberTestData("+10", BigInteger.TEN)),
                new NumberTestDataSupplier("255", () -> new NumberTestData("255", new BigInteger("255"))),
                new NumberTestDataSupplier("-255", () -> new NumberTestData("-255", new BigInteger("-255"))),
                new NumberTestDataSupplier("-12345678", () -> new NumberTestData("-12345678", new BigInteger("-12345678"))),
                new NumberTestDataSupplier("+12345678", () -> new NumberTestData("+12345678", new BigInteger("+12345678")))

        );
    }

    protected List<NumberTestDataSupplier> createDataForVeryLongDecStrings() {
        return Arrays.asList(
                new NumberTestDataSupplier("'0' ** 1292782622", () -> new NumberTestData(new VirtualCharSequence('0', 1_292_782_621 + 1), BigInteger.ZERO)),
                new NumberTestDataSupplier("'9' ** 1292782622", () -> new NumberTestData(new VirtualCharSequence('9', 1_292_782_621 + 1), AbstractNumberParser.VALUE_EXCEEDS_LIMITS, NumberFormatException.class)),
                new NumberTestDataSupplier("'8' ** 646_456_993", () -> new NumberTestData(new VirtualCharSequence('8', 646_456_993), AbstractNumberParser.VALUE_EXCEEDS_LIMITS, NumberFormatException.class)),
                new NumberTestDataSupplier("max input length: '0' ** 1292782621", () -> new NumberTestData(new VirtualCharSequence('0', 1_292_782_621), BigInteger.ZERO)),
                new NumberTestDataSupplier("max input length: '0' ** 1292782620, '7'", () -> new NumberTestData(new VirtualCharSequence("", 0, "", "7", '0', 1_292_782_621), BigInteger.valueOf(7))),
                new NumberTestDataSupplier("'9806543217' ** 1000", () -> new NumberTestData(repeat("9806543217", 1_000), new BigInteger(repeat("9806543217", 1_000), 10))),
                new NumberTestDataSupplier("max input length: '0'**1291782620,'1','0'**100_000", () -> new NumberTestData(
                        new VirtualCharSequence("", 1_292_782_621 - 100_000 - 1, "1", "", '0', 1_292_782_621),
                        BigInteger.valueOf(5).pow(100_000).shiftLeft(100_000)))
        );
    }

    protected List<NumberTestDataSupplier> createDataForVeryLongHexStrings() {
        return Arrays.asList(
        );
    }


    List<NumberTestDataSupplier> createTestData() {
        List<NumberTestDataSupplier> list = new ArrayList<>();
        list.addAll(createDataForIllegalStrings());
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForLegalHexStrings());
        list.addAll(createDataForLegalRadixStrings());
        list.addAll(createDataForIllegalRadixStrings());
        return list;
    }

    List<NumberTestDataSupplier> createLongRunningTestData() {
        List<NumberTestDataSupplier> list = new ArrayList<>();
        if (longRunningTests) {
            list.addAll(createDataForVeryLongDecStrings());
            list.addAll(createDataForVeryLongHexStrings());
        }
        return list;
    }

}
