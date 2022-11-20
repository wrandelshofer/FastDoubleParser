/*
 * @(#)AbstractBigIntegerParserTest.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractBigIntegerParserTest {
    protected List<NumberTestData> createDataForLegalHexStrings() {
        return Arrays.asList(
                new NumberTestData("0x0", BigInteger.ZERO),
                new NumberTestData("0x1", BigInteger.ONE),
                new NumberTestData("0xa", BigInteger.TEN),

                new NumberTestData("0x00", BigInteger.ZERO),
                new NumberTestData("0x01", BigInteger.ONE),
                new NumberTestData("0x00000000", BigInteger.ZERO),
                new NumberTestData("0x00000001", BigInteger.ONE),

                new NumberTestData("0x1", new BigInteger("1", 16)),
                new NumberTestData("0x12", new BigInteger("12", 16)),
                new NumberTestData("0x123", new BigInteger("123", 16)),
                new NumberTestData("0x1234", new BigInteger("1234", 16)),
                new NumberTestData("0x12345", new BigInteger("12345", 16)),
                new NumberTestData("0x123456", new BigInteger("123456", 16)),
                new NumberTestData("0x1234567", new BigInteger("1234567", 16)),
                new NumberTestData("0x12345678", new BigInteger("12345678", 16)),

                new NumberTestData("-0x0", BigInteger.ZERO.negate()),
                new NumberTestData("-0x1", BigInteger.ONE.negate()),
                new NumberTestData("-0xa", BigInteger.TEN.negate()),
                new NumberTestData("0xff", new BigInteger("ff", 16)),
                new NumberTestData("-0xff", new BigInteger("-ff", 16)),
                new NumberTestData("-0x12345678", new BigInteger("-12345678", 16))
        );
    }

    protected List<NumberTestData> createDataForIllegalDecStrings() {
        return Arrays.asList(
                new NumberTestData("'0' ** 1292782622", "0".repeat(1292782621 + 1), new BigInteger("0".repeat(1292782621 + 1)))
        );
    }

    protected List<NumberTestData> createDataForLegalDecStrings() {
        return Arrays.asList(
                new NumberTestData("0", BigInteger.ZERO),
                new NumberTestData("1", BigInteger.ONE),
                new NumberTestData("10", BigInteger.TEN),

                new NumberTestData("00", BigInteger.ZERO),
                new NumberTestData("01", BigInteger.ONE),
                new NumberTestData("00000000", BigInteger.ZERO),
                new NumberTestData("00000001", BigInteger.ONE),

                new NumberTestData("1", new BigInteger("1", 10)),
                new NumberTestData("12", new BigInteger("12", 10)),
                new NumberTestData("123", new BigInteger("123", 10)),
                new NumberTestData("1234", new BigInteger("1234", 10)),
                new NumberTestData("12345", new BigInteger("12345", 10)),
                new NumberTestData("123456", new BigInteger("123456", 10)),
                new NumberTestData("1234567", new BigInteger("1234567", 10)),
                new NumberTestData("12345678", new BigInteger("12345678", 10)),

                new NumberTestData("123456789012345678901234567890",
                        new BigInteger("123456789012345678901234567890", 10)),

                new NumberTestData("-0", BigInteger.ZERO.negate()),
                new NumberTestData("-1", BigInteger.ONE.negate()),
                new NumberTestData("-10", BigInteger.TEN.negate()),
                new NumberTestData("255", new BigInteger("255", 10)),
                new NumberTestData("-255", new BigInteger("-255", 10)),
                new NumberTestData("-12345678", new BigInteger("-12345678", 10)),

                new NumberTestData("9806543217".repeat(1_000), new BigInteger("9806543217".repeat(1_000), 10))
        );
    }

    protected List<NumberTestData> createDataForVeryLongDecStrings() {
        return Arrays.asList(
                new NumberTestData("max input length: '0' ** 1292782621", "0".repeat(1292782621), BigInteger.ZERO),
                new NumberTestData("max input length: '0' ** 1292782620, '7'", "0".repeat(1292782621 - 1) + "7", BigInteger.valueOf(7))
        );
    }


    List<NumberTestData> createRegularTestData() {
        List<NumberTestData> list = new ArrayList<>();
        list.addAll(createDataForLegalDecStrings());
        list.addAll(createDataForLegalHexStrings());
        return list;
    }

}
