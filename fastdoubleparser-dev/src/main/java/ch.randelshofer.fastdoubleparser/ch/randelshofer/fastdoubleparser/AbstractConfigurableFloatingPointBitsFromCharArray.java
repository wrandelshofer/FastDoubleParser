/*
 * @(#)AbstractConfigurableFloatingPointBitsFromCharArray.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.CharTrie;

/**
 * Configurable floating point parser.
 */
abstract class AbstractConfigurableFloatingPointBitsFromCharArray extends AbstractFloatValueParser {
    private final CharDigitSet digitSet;
    private final CharSet minusSignChar;
    private final CharSet plusSignChar;
    private final CharSet decimalSeparator;
    private final CharSet groupingSeparator;
    private final CharTrie nanTrie;
    private final CharTrie infinityTrie;
    private final CharTrie exponentSeparatorTrie;

    public AbstractConfigurableFloatingPointBitsFromCharArray(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = CharDigitSet.copyOf(symbols.digits());
        this.minusSignChar = CharSet.copyOf(symbols.minusSign(), ignoreCase);
        this.exponentSeparatorTrie = CharTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSignChar = CharSet.copyOf(symbols.plusSign(), ignoreCase);
        this.nanTrie = CharTrie.copyOf(symbols.nan(), ignoreCase);
        this.infinityTrie = CharTrie.copyOf(symbols.infinity(), ignoreCase);
    }

    /**
     * @return a NaN constant in the specialized type wrapped in a {@code long}
     */
    abstract long nan();

    /**
     * @return a negative infinity constant in the specialized type wrapped in a
     * {@code long}
     */
    abstract long negativeInfinity();

    private boolean isDecimalSeparator(char ch) {
        return decimalSeparator.containsKey(ch);
    }

    private boolean isGroupingSeparator(char ch) {
        return groupingSeparator.containsKey(ch);
    }

    /**
     * Parses a {@code FloatingPointLiteral} production with optional leading and trailing
     * white space.
     * <blockquote>
     * <dl>
     * <dt><i>FloatingPointLiteralWithWhiteSpace:</i></dt>
     * <dd><i>[WhiteSpace] FloatingPointLiteral [WhiteSpace]</i></dd>
     * </dl>
     * </blockquote>
     * See {@link JavaDoubleParser} for the grammar of
     * {@code FloatingPointLiteral}.
     *
     * @param str    a string containing a {@code FloatingPointLiteralWithWhiteSpace}
     * @param offset start offset of {@code FloatingPointLiteralWithWhiteSpace} in {@code str}
     * @param length length of {@code FloatingPointLiteralWithWhiteSpace} in {@code str}
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public final long parseFloatingPointLiteral(char[] str, int offset, int length) {
        final int endIndex = checkBounds(str.length, offset, length);

        // Skip leading format characters
        // -------------------
        int index = skipFormatCharacters(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        char ch = str[index];

        // Parse optional sign
        // -------------------
        final boolean isNegative = isMinusSign(ch);
        if (isNegative || isPlusSign(ch)) {
            ++index;
            if (index == endIndex) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        // Parse significand
        // -----------------
        // Note: a multiplication by a constant is cheaper than an
        //       arbitrary integer multiplication.
        long significand = 0;// significand is treated as an unsigned long
        final int significandStartIndex = index;
        int decimalSeparatorIndex = -1;
        int integerDigitCount = -1;
        int groupingCount = 0;
        boolean illegal = false;

        for (; index < endIndex; index++) {
            ch = str[index];
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + digit;
            } else if (isDecimalSeparator(ch)) {
                illegal |= integerDigitCount >= 0;
                decimalSeparatorIndex = index;
                integerDigitCount = index - significandStartIndex - groupingCount;
            } else if (isGroupingSeparator(ch)) {
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
        int count = exponentSeparatorTrie.match(str, index, endIndex);
        if (count > 0) {
            index += count;
            index = skipFormatCharacters(str, index, endIndex);
            ch = charAt(str, index, endIndex);
            boolean isExponentNegative = isMinusSign(ch);
            if (isExponentNegative || isPlusSign(ch)) {
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

        // Parse NaN or Infinity (this occurs rarely)
        // ---------------------
        if (!illegal && digitCount == 0) {
            return parseNaNOrInfinity(str, index, endIndex, isNegative);
        }

        // Check if FloatingPointLiteral is complete
        // ------------------------
        if (illegal || index < endIndex) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }

        // Re-parse significand in case of a potential overflow
        // -----------------------------------------------
        final boolean isSignificandTruncated;
        int exponentOfTruncatedSignificand;
        if (digitCount > 19) {
            int truncatedDigitCount = 0;
            significand = 0;
            for (index = significandStartIndex; index < significandEndIndex; index++) {
                ch = str[index];
                int digit = digitSet.toDigit(ch);
                if (digit < 10) {
                    if (Long.compareUnsigned(significand, AbstractFloatValueParser.MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
                        significand = 10 * significand + digit;
                        truncatedDigitCount++;
                    } else {
                        break;
                    }
                }
            }
            isSignificandTruncated = index < significandEndIndex;
            exponentOfTruncatedSignificand = integerDigitCount - truncatedDigitCount + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return valueOfFloatLiteral(str, significandStartIndex, decimalSeparatorIndex,
                decimalSeparatorIndex + 1, significandEndIndex,
                isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand, expNumber, offset, endIndex);
    }

    private boolean isMinusSign(char c) {
        return minusSignChar.containsKey(c);
    }

    private boolean isPlusSign(char c) {
        return plusSignChar.containsKey(c);
    }

    /**
     * Skips all format characters.
     *
     * @param str      a string
     * @param index    start index (inclusive) of the optional white space
     * @param endIndex end index (exclusive) of the optional white space
     * @return index after the optional format character
     */
    private static int skipFormatCharacters(char[] str, int index, int endIndex) {
        while (index < endIndex && Character.getType(str[index]) == Character.FORMAT) {
            index++;
        }
        return index;
    }

    private long parseNaNOrInfinity(char[] str, int index, int endIndex, boolean isNegative) {
        int nanMatch = nanTrie.match(str, index, endIndex);
        if (nanMatch > 0) {
            if (index + nanMatch == endIndex) {
                return nan();
            }
        }
        int infinityMatch = infinityTrie.match(str, index, endIndex);
        if (infinityMatch > 0) {
            if (index + infinityMatch == endIndex) {
                return isNegative ? negativeInfinity() : positiveInfinity();
            }
        }
        throw new NumberFormatException(SYNTAX_ERROR);
    }

    /**
     * @return a positive infinity constant in the specialized type wrapped in a
     * {@code long}
     */
    abstract long positiveInfinity();

    /**
     * Computes a float value from the given components of a decimal float
     * literal.
     *
     * @param str                            the string that contains the float literal (and maybe more)
     * @param integerStartIndex              the start index (inclusive) of the integer part of the significand
     *                                       inside the string
     * @param integerEndIndex                the end index (exclusive) of the integer part of the significand
     *                                       the string
     * @param fractionStartIndex             the start index (inclusive) of the fraction part of the significand
     *                                       inside the string
     * @param fractionEndIndex               the end index (exclusive) of the fraction part of the significand
     *                                       the string
     * @param isSignificandNegative          whether the significand value is negative
     * @param significand                    the significand of the float value (can be truncated)
     * @param exponent                       the exponent of the float value considering the significand
     * @param isSignificandTruncated         whether the significand is truncated
     * @param exponentOfTruncatedSignificand the exponent value of the truncated
     *                                       significand
     * @param exponentValue                  the exponent of the float value without considering the significand
     * @param startIndex
     * @param endIndex
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    abstract long valueOfFloatLiteral(char[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative,
                                      long significand, int exponent, boolean isSignificandTruncated,
                                      int exponentOfTruncatedSignificand, int exponentValue, int startIndex, int endIndex);

    protected double slowPathToDouble(char[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, int exponentValue) {
        return SlowDoubleConversionPath.toDouble(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue);
    }


}
