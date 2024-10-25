/*
 * @(#)JavaBigDecimalFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.CharTrie;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NavigableMap;

import static ch.randelshofer.fastdoubleparser.FastIntegerMath.computePowerOfTen;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.createPowersOfTenFloor16Map;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.fillPowersOfNFloor16Recursive;


/**
 * Parses a {@link BigDecimal} from a {@code byte} array.
 */
final class ConfigurableBigDecimalFromCharSequence extends AbstractBigDecimalParser {
    private final CharDigitSet digitSet;
    private final CharSet minusSignChar;
    private final CharSet plusSignChar;
    private final CharSet decimalSeparator;
    private final CharSet groupingSeparator;
    private final CharTrie exponentSeparatorTrie;

    /**
     * Creates a new instance.
     */
    public ConfigurableBigDecimalFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = CharDigitSet.copyOf(symbols.digits());
        this.minusSignChar = CharSet.copyOf(symbols.minusSign(), ignoreCase);
        this.exponentSeparatorTrie = CharTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSignChar = CharSet.copyOf(symbols.plusSign(), ignoreCase);
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
    public BigDecimal parseBigDecimalString(CharSequence str, int offset, int length) {
        try {
            final int endIndex = checkBounds(str.length(), offset, length);
            if (hasManyDigits(length)) {
                return parseBigDecimalStringWithManyDigits(str, offset, length);
            }

            // Skip leading format characters
            // -------------------
            int index = skipFormatCharacters(str, offset, endIndex);
            if (index == endIndex) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
            char ch = str.charAt(index);

            // Parse optional sign
            // -------------------
            final boolean isNegative = minusSignChar.containsKey(ch);
            if (isNegative || plusSignChar.containsKey(ch)) {
                ch = charAt(str, ++index, endIndex);
                if (ch == 0) {
                    throw new NumberFormatException(SYNTAX_ERROR);
                }
            }

            // Parse significand
            long significand = 0;// significand is treated as an unsigned long
            final int significandStartIndex = index;
            int decimalSeparatorIndex = -1;
            int integerDigitCount = -1;
            int groupingCount = 0;
            boolean illegal = false;

            for (; index < endIndex; index++) {
                ch = str.charAt(index);
                int digit = digitSet.toDigit(ch);
                if (digit < 10) {
                    // This might overflow, we deal with it later.
                    significand = 10 * significand + digit;
                } else if (decimalSeparator.containsKey(ch)) {
                    illegal |= integerDigitCount >= 0;
                    decimalSeparatorIndex = index;
                    integerDigitCount = index - significandStartIndex - groupingCount;
                } else if (groupingSeparator.containsKey(ch)) {
                    illegal |= decimalSeparatorIndex != -1;
                    groupingCount++;
                } else {
                    break;
                }
            }
            final int digitCount;
            final int significandEndIndex = index;
            int exponent;
            if (integerDigitCount < 0) {
                integerDigitCount = digitCount = significandEndIndex - significandStartIndex - groupingCount;
                decimalSeparatorIndex = significandEndIndex;
                exponent = 0;
            } else {
                digitCount = significandEndIndex - significandStartIndex - 1 - groupingCount;
                exponent = integerDigitCount - digitCount;
            }
            illegal |= digitCount == 0 && significandEndIndex > significandStartIndex;

            // Parse exponent number
            // ---------------------
            int expNumber = 0;
            int exponentSeparatorIndex = -1;
            int count = exponentSeparatorTrie.match(str, index, endIndex);
            if (count > 0) {
                exponentSeparatorIndex = index;
                index += count;
                index = skipFormatCharacters(str, index, endIndex);
                ch = charAt(str, index, endIndex);
                boolean isExponentNegative = minusSignChar.containsKey(ch);
                if (isExponentNegative || plusSignChar.containsKey(ch)) {
                    ch = charAt(str, ++index, endIndex);
                }
                int digit = digitSet.toDigit(ch);
                illegal |= digit >= 10;
                do {
                    // Guard against overflow
                    if (expNumber < AbstractFloatValueParser.MAX_EXPONENT_NUMBER) {
                        expNumber = 10 * expNumber + digit;
                    }
                    ch = charAt(str, ++index, endIndex);
                    digit = digitSet.toDigit(ch);
                } while (digit < 10);
                if (isExponentNegative) {
                    expNumber = -expNumber;
                }
                exponent += expNumber;
            }
            illegal |= digitCount == 0;
            checkParsedBigDecimalBounds(illegal, index, endIndex, digitCount, exponent);
            if (digitCount < 19) {
                return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int) exponent);
            }
            return valueOfBigDecimalString(str, significandStartIndex, decimalSeparatorIndex, decimalSeparatorIndex + 1, exponentSeparatorIndex, isNegative, (int) expNumber);
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    /**
     * Parses a big decimal string that has many digits.
     */
    BigDecimal parseBigDecimalStringWithManyDigits(CharSequence str, int offset, int length) {
        final int integerPartIndex;
        final int nonZeroIntegerPartIndex;
        int nonZeroFractionalPartIndex = -1;
        int decimalPointIndex = -1;
        final int exponentIndicatorIndex;

        final int endIndex = offset + length;
        int index = offset;
        char ch = charAt(str, index, endIndex);
        boolean illegal = false;

        // Parse optional sign
        // -------------------
        final boolean isNegative = minusSignChar.containsKey(ch);
        if (isNegative || plusSignChar.containsKey(ch)) {
            ch = charAt(str, ++index, endIndex);
            if (ch == 0) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        // Count digits of significand
        // -----------------
        // skip leading zeroes
        integerPartIndex = index;
        // swarLimit: We can process blocks of eight chars with SWAR, we must process the remaining chars individually.
        int swarLimit = Math.min(endIndex - 8, 1 << 30);
        while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
            index += 8;
        }
        char zeroChar = digitSet.getZeroChar();
        while (index < endIndex && str.charAt(index) == zeroChar) {
            index++;
        }
        // Count digits of integer part
        // FIXME skip over groupingSeparator!
        nonZeroIntegerPartIndex = index;
        while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
            index += 8;
        }
        while (index < endIndex && FastDoubleSwar.isDigit(ch = str.charAt(index))) {
            index++;
        }
        if (decimalSeparator.containsKey(ch)) {
            decimalPointIndex = index++;
            // skip leading zeroes
            while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
                index += 8;
            }
            while (index < endIndex && str.charAt(index) == zeroChar) {
                index++;
            }
            nonZeroFractionalPartIndex = index;
            // Count digits of fraction part
            while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
                index += 8;
            }
            while (index < endIndex && FastDoubleSwar.isDigit(ch = str.charAt(index))) {
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
        int count = exponentSeparatorTrie.match(str, index, endIndex);
        if (count > 0) {
            index += count;
            exponentIndicatorIndex = index;
            ch = charAt(str, index, endIndex);
            boolean isExponentNegative = minusSignChar.containsKey(ch);
            if (isExponentNegative || plusSignChar.containsKey(ch)) {
                ch = charAt(str, ++index, endIndex);
            }
            int digit = digitSet.toDigit(ch);
            illegal |= digit >= 10;
            do {
                // Guard against overflow
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * (expNumber) + digit;
                }
                ch = charAt(str, ++index, endIndex);
                digit = digitSet.toDigit(ch);
            } while (digit < 10);
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
     * Skips all format characters.
     *
     * @param str      a string
     * @param index    start index (inclusive) of the optional white space
     * @param endIndex end index (exclusive) of the optional white space
     * @return index after the optional format character
     */
    private static int skipFormatCharacters(CharSequence str, int index, int endIndex) {
        while (index < endIndex && Character.getType(str.charAt(index)) == Character.FORMAT) {
            index++;
        }
        return index;
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
    BigDecimal valueOfBigDecimalString(CharSequence str, int integerPartIndex, int decimalPointIndex, int nonZeroFractionalPartIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        int fractionDigitsCount = exponentIndicatorIndex - decimalPointIndex - 1;
        int nonZeroFractionDigitsCount = exponentIndicatorIndex - nonZeroFractionalPartIndex;
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
                integerPart = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, powersOfTen, RECURSION_THRESHOLD);
            } else {
                integerPart = ParseDigitsTaskCharSequence.parseDigitsIterative(str, integerPartIndex, decimalPointIndex);
            }
        } else {
            integerPart = BigInteger.ZERO;
        }

        // If there is a fraction part, we parse it using a recursive algorithm.
        // The recursive algorithm needs a map with powers of ten, if we have more than RECURSION_THRESHOLD digits.
        if (fractionDigitsCount > 0) {
            BigInteger fractionalPart;
            if (nonZeroFractionDigitsCount > RECURSION_THRESHOLD) {
                if (powersOfTen == null) {
                    powersOfTen = createPowersOfTenFloor16Map();
                }
                fillPowersOfNFloor16Recursive(powersOfTen, nonZeroFractionalPartIndex, exponentIndicatorIndex);
                fractionalPart = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, nonZeroFractionalPartIndex, exponentIndicatorIndex, powersOfTen, RECURSION_THRESHOLD);
            } else {
                fractionalPart = ParseDigitsTaskCharSequence.parseDigitsIterative(str, nonZeroFractionalPartIndex, exponentIndicatorIndex);
            }
            // If the integer part is 0, we can just use the fractional part.
            if (integerPart.signum() == 0) {
                significand = fractionalPart;
            } else {
                BigInteger integerFactor = computePowerOfTen(powersOfTen, fractionDigitsCount);
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