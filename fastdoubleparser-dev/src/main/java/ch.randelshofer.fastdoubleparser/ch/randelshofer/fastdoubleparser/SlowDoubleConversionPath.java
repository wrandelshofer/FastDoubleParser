/*
 * @(#)SlowDoubleConversionPath.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;

import java.math.BigDecimal;
import java.math.BigInteger;

class SlowDoubleConversionPath {
    private SlowDoubleConversionPath() {
    }

    private final static int[] powersOfTen = {0, 10, 100, 1000, 1_0000, 1_0000_0, 1_0000_00, 1_0000_000, 1_0000_0000};

    static double toDouble(CharSequence str, CharDigitSet digitSet, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, long exponentValue) {
        double v = toBigDecimal(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, FastDoubleMath.MAX_REQUIRED_DIGITS, exponentValue).doubleValue();
        return isSignificandNegative ? -v : v;
    }

    static BigDecimal toBigDecimal(CharSequence str, CharDigitSet digitSet, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, int maxRequiredDigits, long exponentValue) {

        // skip leading zeroes in integer part
        for (; integerStartIndex < integerEndIndex; integerStartIndex++) {
            char ch = str.charAt(integerStartIndex);
            int digit = digitSet.toDigit(ch);
            boolean isDigit = digit < 10;
            if (isDigit) {
                if (digit > 0) {
                    break;
                }
            }
        }
        // skip leading zeroes in fraction part
        int skippedFractionDigits = 0;
        if (integerStartIndex == integerEndIndex) {
            for (; fractionStartIndex < fractionEndIndex; fractionStartIndex++) {
                char ch = str.charAt(fractionStartIndex);
                int digit = digitSet.toDigit(ch);
                if (digit > 0 && digit < 10) {
                    break;
                }
                skippedFractionDigits++;
            }
        }

        // The integer part may contain grouping characters.
        // This is why we can not definitely compute the number of digits.
        int estimatedNumDigits = integerEndIndex - integerStartIndex + fractionEndIndex - fractionStartIndex;
        BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(Math.min(estimatedNumDigits, maxRequiredDigits)));

        // collect integer digits
        int numIntegerDigits = 0;
        int acc = 0;
        int i;
        for (i = integerStartIndex; i < integerEndIndex && numIntegerDigits < maxRequiredDigits; i++) {
            char ch = str.charAt(i);
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                acc = acc * 10 + digit;
                numIntegerDigits++;
                if ((numIntegerDigits % 8) == 0) {
                    b.fma(1_0000_0000, acc);
                    acc = 0;
                }
            }
        }
        int mul = powersOfTen[numIntegerDigits % 8];
        if (mul != 0) b.fma(mul, acc);


        // skip remaining integer digits
        int skippedIntegerDigits = 0;
        for (; i < integerEndIndex; i++) {
            char ch = str.charAt(i);
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                skippedIntegerDigits++;
            }

        }

        // Now we can determine how many fraction digits we want to skip
        fractionEndIndex = Math.min(fractionEndIndex, fractionStartIndex + Math.max(maxRequiredDigits - numIntegerDigits, 0));

        // collect fraction digits
        int numFractionDigits = 0;
        acc = 0;
        for (i = fractionStartIndex; i < fractionEndIndex; i++) {
            char ch = str.charAt(i);
            acc = acc * 10 + digitSet.toDigit(ch);
            numFractionDigits++;
            if ((numFractionDigits % 8) == 0) {
                b.fma(1_0000_0000, acc);
                acc = 0;
            }
        }
        mul = powersOfTen[numFractionDigits % 8];
        if (mul != 0) b.fma(mul, acc);

        int exponent = (int) (exponentValue + skippedIntegerDigits - numFractionDigits - skippedFractionDigits);

        BigInteger bigInteger = b.toBigInteger();

        return new BigDecimal(bigInteger, -exponent);
    }

    static double toDouble(char[] str, CharDigitSet digitSet, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, long exponentValue) {
        double v = toBigDecimal(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, FastDoubleMath.MAX_REQUIRED_DIGITS, exponentValue).doubleValue();
        return isSignificandNegative ? -v : v;
    }

    static double toDouble(byte[] str, ByteDigitSet digitSet, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, long exponentValue) {
        double v = toBigDecimal(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, FastDoubleMath.MAX_REQUIRED_DIGITS, exponentValue).doubleValue();
        return isSignificandNegative ? -v : v;
    }

    static BigDecimal toBigDecimal(char[] str, CharDigitSet digitSet, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, int maxRequiredDigits, long exponentValue) {

        // skip leading zeroes in integer part
        for (; integerStartIndex < integerEndIndex; integerStartIndex++) {
            char ch = str[integerStartIndex];
            int digit = digitSet.toDigit(ch);
            boolean isDigit = digit < 10;
            if (isDigit) {
                if (digit > 0) {
                    break;
                }
            }
        }
        // skip leading zeroes in fraction part
        int skippedFractionDigits = 0;
        if (integerStartIndex == integerEndIndex) {
            for (; fractionStartIndex < fractionEndIndex; fractionStartIndex++) {
                char ch = str[fractionStartIndex];
                int digit = digitSet.toDigit(ch);
                if (digit > 0 && digit < 10) {
                    break;
                }
                skippedFractionDigits++;
            }
        }

        // The integer part may contain grouping characters.
        // This is why we can not definitely compute the number of digits.
        int estimatedNumDigits = integerEndIndex - integerStartIndex + fractionEndIndex - fractionStartIndex;
        BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(Math.min(estimatedNumDigits, maxRequiredDigits)));

        // collect integer digits
        int numIntegerDigits = 0;
        int acc = 0;
        int i;
        for (i = integerStartIndex; i < integerEndIndex && numIntegerDigits < maxRequiredDigits; i++) {
            char ch = str[i];
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                acc = acc * 10 + digit;
                numIntegerDigits++;
                if ((numIntegerDigits % 8) == 0) {
                    b.fma(1_0000_0000, acc);
                    acc = 0;
                }
            }
        }
        int mul = powersOfTen[numIntegerDigits % 8];
        if (mul != 0) b.fma(mul, acc);


        // skip remaining integer digits
        int skippedIntegerDigits = 0;
        for (; i < integerEndIndex; i++) {
            char ch = str[i];
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                skippedIntegerDigits++;
            }

        }

        // Now we can determine how many fraction digits we want to skip
        fractionEndIndex = Math.min(fractionEndIndex, fractionStartIndex + Math.max(maxRequiredDigits - numIntegerDigits, 0));

        // collect fraction digits
        int numFractionDigits = 0;
        acc = 0;
        for (i = fractionStartIndex; i < fractionEndIndex; i++) {
            char ch = str[i];
            acc = acc * 10 + digitSet.toDigit(ch);
            numFractionDigits++;
            if ((numFractionDigits % 8) == 0) {
                b.fma(1_0000_0000, acc);
                acc = 0;
            }
        }
        mul = powersOfTen[numFractionDigits % 8];
        if (mul != 0) b.fma(mul, acc);

        int exponent = (int) (exponentValue + skippedIntegerDigits - numFractionDigits - skippedFractionDigits);

        BigInteger bigInteger = b.toBigInteger();

        return new BigDecimal(bigInteger, -exponent);
    }
    static BigDecimal toBigDecimal(byte[] str, ByteDigitSet digitSet, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, int maxRequiredDigits, long exponentValue) {

        // skip leading zeroes in integer part
        for (; integerStartIndex < integerEndIndex; integerStartIndex++) {
            byte ch = str[integerStartIndex];
            int digit = digitSet.toDigit(ch);
            boolean isDigit = digit < 10;
            if (isDigit) {
                if (digit > 0) {
                    break;
                }
            }
        }
        // skip leading zeroes in fraction part
        int skippedFractionDigits = 0;
        if (integerStartIndex == integerEndIndex) {
            for (; fractionStartIndex < fractionEndIndex; fractionStartIndex++) {
                byte ch = str[fractionStartIndex];
                int digit = digitSet.toDigit(ch);
                if (digit > 0 && digit < 10) {
                    break;
                }
                skippedFractionDigits++;
            }
        }

        // The integer part may contain grouping characters.
        // This is why we can not definitely compute the number of digits.
        int estimatedNumDigits = integerEndIndex - integerStartIndex + fractionEndIndex - fractionStartIndex;
        BigSignificand b = new BigSignificand(FastIntegerMath.estimateNumBits(Math.min(estimatedNumDigits, maxRequiredDigits)));

        // collect integer digits
        int numIntegerDigits = 0;
        int acc = 0;
        int i;
        for (i = integerStartIndex; i < integerEndIndex && numIntegerDigits < maxRequiredDigits; i++) {
            byte ch = str[i];
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                acc = acc * 10 + digit;
                numIntegerDigits++;
                if ((numIntegerDigits % 8) == 0) {
                    b.fma(1_0000_0000, acc);
                    acc = 0;
                }
            }
        }
        int mul = powersOfTen[numIntegerDigits % 8];
        if (mul != 0) b.fma(mul, acc);


        // skip remaining integer digits
        int skippedIntegerDigits = 0;
        for (; i < integerEndIndex; i++) {
            byte ch = str[i];
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                skippedIntegerDigits++;
            }

        }

        // Now we can determine how many fraction digits we want to skip
        fractionEndIndex = Math.min(fractionEndIndex, fractionStartIndex + Math.max(maxRequiredDigits - numIntegerDigits, 0));

        // collect fraction digits
        int numFractionDigits = 0;
        acc = 0;
        for (i = fractionStartIndex; i < fractionEndIndex; i++) {
            byte ch = str[i];
            acc = acc * 10 + digitSet.toDigit(ch);
            numFractionDigits++;
            if ((numFractionDigits % 8) == 0) {
                b.fma(1_0000_0000, acc);
                acc = 0;
            }
        }
        mul = powersOfTen[numFractionDigits % 8];
        if (mul != 0) b.fma(mul, acc);

        int exponent = (int) (exponentValue + skippedIntegerDigits - numFractionDigits - skippedFractionDigits);

        BigInteger bigInteger = b.toBigInteger();

        return new BigDecimal(bigInteger, -exponent);
    }


}
