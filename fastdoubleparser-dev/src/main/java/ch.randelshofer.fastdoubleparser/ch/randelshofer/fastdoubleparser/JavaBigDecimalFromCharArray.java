/*
 * @(#)JavaBigDecimalFromCharArray.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NavigableMap;

import static ch.randelshofer.fastdoubleparser.FastIntegerMath.*;
import static ch.randelshofer.fastdoubleparser.ParseDigitsTaskCharArray.RECURSION_THRESHOLD;


/**
 * Parses a {@code double} from a {@code byte} array.
 */
final class JavaBigDecimalFromCharArray extends AbstractBigDecimalParser {
    /**
     * Creates a new instance.
     */
    public JavaBigDecimalFromCharArray() {

    }

    /**
     * Parses a {@code BigDecimalString} as specified in {@link JavaBigDecimalParser}.
     *
     * @param str    the input string
     * @param offset start of the input data
     * @param length length of the input data
     * @return the parsed {@link BigDecimal}
     * @throws NullPointerException     if str is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the input string can not be parsed successfully
     */
    public BigDecimal parseBigDecimalString(char[] str, int offset, int length) {
        try {
            final int endIndex = checkBounds(str.length, offset, length);
            if (hasManyDigits(length)) {
                return parseBigDecimalStringWithManyDigits(str, offset, length);
            }
            long significand = 0L;
            final int integerPartIndex;
            int decimalPointIndex = -1;
            final int exponentIndicatorIndex;

            int index = offset;
            char ch = charAt(str, index, endIndex);
            boolean illegal = false;


            // Parse optional sign
            // -------------------
            final boolean isNegative = ch == '-';
            if (isNegative || ch == '+') {
                ch = charAt(str, ++index, endIndex);
                if (ch == 0) {
                    throw new NumberFormatException(SYNTAX_ERROR);
                }
            }

            // Parse significand
            integerPartIndex = index;
            for (; index < endIndex; index++) {
                ch = str[index];
                if (FastDoubleSwar.isDigit(ch)) {
                    // This might overflow, we deal with it later.
                    significand = 10 * (significand) + ch - '0';
                } else if (ch == '.') {
                    illegal |= decimalPointIndex >= 0;
                    decimalPointIndex = index;
                    for (; index < endIndex - 4; index += 4) {
                        int digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1);
                        if (digits < 0) {
                            break;
                        }
                        // This might overflow, we deal with it later.
                        significand = 10_000L * significand + digits;
                    }
                } else {
                    break;
                }
            }

            final int digitCount;
            final int significandEndIndex = index;
            long exponent;
            if (decimalPointIndex < 0) {
                digitCount = significandEndIndex - integerPartIndex;
                decimalPointIndex = significandEndIndex;
                exponent = 0;
            } else {
                digitCount = significandEndIndex - integerPartIndex - 1;
                exponent = decimalPointIndex - significandEndIndex + 1;
            }

            // Parse exponent number
            // ---------------------
            long expNumber = 0;
            if ((ch | 0x20) == 'e') {// equals ignore case
                exponentIndicatorIndex = index;
                ch = charAt(str, ++index, endIndex);
                boolean isExponentNegative = ch == '-';
                if (isExponentNegative || ch == '+') {
                    ch = charAt(str, ++index, endIndex);
                }
                illegal |= !FastDoubleSwar.isDigit(ch);
                do {
                    // Guard against overflow
                    if (expNumber < MAX_EXPONENT_NUMBER) {
                        expNumber = 10 * (expNumber) + ch - '0';
                    }
                    ch = charAt(str, ++index, endIndex);
                } while (FastDoubleSwar.isDigit(ch));
                if (isExponentNegative) {
                    expNumber = -expNumber;
                }
                exponent += expNumber;
            } else {
                exponentIndicatorIndex = endIndex;
            }
            illegal |= digitCount == 0;
            checkParsedBigDecimalBounds(illegal, index, endIndex, digitCount, exponent);
            if (digitCount < 19) {
                return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int) exponent);
            }
            return valueOfBigDecimalString(str, integerPartIndex, decimalPointIndex, decimalPointIndex + 1, exponentIndicatorIndex, isNegative, (int) exponent);
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    /**
     * Parses a big decimal string that has many digits.
     */
    BigDecimal parseBigDecimalStringWithManyDigits(char[] str, int offset, int length) {
        final int nonZeroIntegerPartIndex;
        final int integerPartIndex;
        int nonZeroFractionalPartIndex = -1;
        int decimalPointIndex = -1;
        final int exponentIndicatorIndex;

        final int endIndex = offset + length;
        int index = offset;
        char ch = charAt(str, index, endIndex);
        boolean illegal = false;

        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = charAt(str, ++index, endIndex);
            if (ch == 0) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        // Count digits of significand
        // -----------------
        integerPartIndex = index;
        int swarLimit = Math.min(endIndex - 8, 1 << 30);
        while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
            index += 8;
        }
        while (index < endIndex && str[index] == '0') {
            index++;
        }
        // Count digits of integer part
        nonZeroIntegerPartIndex = index;
        while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
            index += 8;
        }
        while (index < endIndex && FastDoubleSwar.isDigit(ch = str[index])) {
            index++;
        }
        if (ch == '.') {
            decimalPointIndex = index++;
            // skip leading zeroes
            while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
                index += 8;
            }
            while (index < endIndex && str[index] == '0') {
                index++;
            }
            nonZeroFractionalPartIndex = index;
            // Count digits of fraction part
            while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
                index += 8;
            }
            while (index < endIndex && FastDoubleSwar.isDigit(ch = str[index])) {
                index++;
            }
        }

        final int digitCountWithoutLeadingZeros;
        final int significandEndIndex = index;
        long exponent;
        if (decimalPointIndex < 0) {
            digitCountWithoutLeadingZeros = significandEndIndex - nonZeroIntegerPartIndex;
            decimalPointIndex = significandEndIndex;
            nonZeroFractionalPartIndex = significandEndIndex;
            exponent = 0;
        } else {
            digitCountWithoutLeadingZeros = nonZeroIntegerPartIndex == decimalPointIndex
                    ? significandEndIndex - nonZeroFractionalPartIndex
                    : significandEndIndex - nonZeroIntegerPartIndex - 1;
            exponent = decimalPointIndex - significandEndIndex + 1;
        }

        // Parse exponent number
        // ---------------------
        long expNumber = 0;
        if ((ch | 0x20) == 'e') {// equals ignore case
            exponentIndicatorIndex = index;
            ch = charAt(str, ++index, endIndex);
            boolean isExponentNegative = ch == '-';
            if (isExponentNegative || ch == '+') {
                ch = charAt(str, ++index, endIndex);
            }
            illegal = !FastDoubleSwar.isDigit(ch);
            do {
                // Guard against overflow
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * (expNumber) + ch - '0';
                }
                ch = charAt(str, ++index, endIndex);
            } while (FastDoubleSwar.isDigit(ch));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        } else {
            exponentIndicatorIndex = endIndex;
        }
        illegal |= integerPartIndex == decimalPointIndex && decimalPointIndex == exponentIndicatorIndex;
        checkParsedBigDecimalBounds(illegal, index, endIndex, digitCountWithoutLeadingZeros, exponent);

        return valueOfBigDecimalString(str, nonZeroIntegerPartIndex, decimalPointIndex, nonZeroFractionalPartIndex, exponentIndicatorIndex, isNegative, (int) exponent);
    }


    /**
     * Parses a big decimal string after we have identified the parts of the significand,
     * and after we have obtained the exponent value.
     * <pre>
     *       integerPartIndex
     *       │  decimalPointIndex
     *       │  │  nonZeroFractionalPartIndex
     *       │  │  │  exponentIndicatorIndex
     *       ↓  ↓  ↓  ↓
     *     "-123.00456e-789"
     *
     * </pre>
     *
     * @param str                        the input string
     * @param integerPartIndex           the start index of the integer part of the significand
     * @param decimalPointIndex          the index of the decimal point in the significand (same as exponentIndicatorIndex
     *                                   if there is no decimal point)
     * @param nonZeroFractionalPartIndex the start index of the non-zero fractional part of the significand
     * @param exponentIndicatorIndex     the index of the exponent indicator (same as end of string if there is no
     *                                   exponent indicator)
     * @param isNegative                 indicates that the significand is negative
     * @param exponent                   the exponent value
     * @return the parsed big decimal
     */
    private BigDecimal valueOfBigDecimalString(char[] str, int integerPartIndex, int decimalPointIndex, int nonZeroFractionalPartIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        int integerExponent = exponentIndicatorIndex - decimalPointIndex - 1;
        int fractionDigitsCount = exponentIndicatorIndex - nonZeroFractionalPartIndex;
        int integerDigitsCount = decimalPointIndex - integerPartIndex;
        NavigableMap<Integer, BigInteger> powersOfTen = null;

        // Parse the significand
        // ---------------------
        BigInteger significand;

        // If there is an integer part, we parse it using a recursive algorithm.
        // The recursive algorithm needs a map with powers of ten, if we have more than RECURSION_THRESHOLD digits.
        BigInteger integerPart;
        if (integerDigitsCount > 0) {
            if (integerDigitsCount > RECURSION_THRESHOLD) {
                powersOfTen = createPowersOfTenFloor16Map();
                fillPowersOfNFloor16Recursive(powersOfTen, integerPartIndex, decimalPointIndex);
                integerPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, powersOfTen);
            } else {
                integerPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, null);
            }
        } else {
            integerPart = BigInteger.ZERO;
        }

        // If there is a fraction part, we parse it using a recursive algorithm.
        // The recursive algorithm needs a map with powers of ten, if we have more than RECURSION_THRESHOLD digits.
        if (fractionDigitsCount > 0) {
            BigInteger fractionalPart;
            if (fractionDigitsCount > RECURSION_THRESHOLD) {
                if (powersOfTen == null) {
                    powersOfTen = createPowersOfTenFloor16Map();
                }
                fillPowersOfNFloor16Recursive(powersOfTen, decimalPointIndex + 1, exponentIndicatorIndex);
                fractionalPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, decimalPointIndex + 1, exponentIndicatorIndex, powersOfTen);
            } else {
                fractionalPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, decimalPointIndex + 1, exponentIndicatorIndex, null);
            }
            // If the integer part is not 0, we combine it with the fraction part.
            if (integerPart.signum() == 0) {
                significand = fractionalPart;
            } else {
                BigInteger integerFactor = computePowerOfTen(powersOfTen, integerExponent);
                significand = FftMultiplier.multiply(integerPart, integerFactor).add(fractionalPart);
            }
        } else {
            significand = integerPart;
        }

        // Combine the significand with the sign and the exponent
        // ------------------------------------------------------
        return new BigDecimal(isNegative ? significand.negate() : significand, -exponent);
    }
}