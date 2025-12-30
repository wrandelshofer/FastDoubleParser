/*
 * @(#)AbstractConfigurableFloatingPointBitsFromCharArray.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;
import ch.randelshofer.fastdoubleparser.bte.ByteTrie;

/**
 * Configurable floating point parser for input data given in UTF-8.
 */
abstract class AbstractConfigurableFloatingPointBitsFromByteArrayUtf8 extends AbstractFloatValueParser {
    private final ByteDigitSet digitSet;
    private final ByteTrie minusSign;
    private final ByteTrie plusSign;
    private final ByteTrie decimalSeparator;
    private final ByteTrie groupingSeparator;
    private final ByteTrie nan;
    private final ByteTrie infinity;
    private final ByteTrie exponentSeparator;

    public AbstractConfigurableFloatingPointBitsFromByteArrayUtf8(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = ByteTrie.copyOfChars(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = ByteTrie.copyOfChars(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = ByteDigitSet.copyOf(symbols.digits());
        this.minusSign = ByteTrie.copyOfChars(symbols.minusSign(), ignoreCase);
        this.exponentSeparator = ByteTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSign = ByteTrie.copyOfChars(symbols.plusSign(), ignoreCase);
        this.nan = ByteTrie.copyOf(symbols.nan(), ignoreCase);
        this.infinity = ByteTrie.copyOf(symbols.infinity(), ignoreCase);
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
    public final long parseFloatingPointLiteral(byte[] str, int offset, int length) {
        final int endIndex = checkBounds(str.length, offset, length);
        int index = offset;

        // Parse optional sign before significand
        // -------------------
        int matchCount;
        boolean isNegative = (matchCount = minusSign.match(str, index, endIndex)) > 0;
        if (isNegative) {
            index += matchCount;
        } else {
            index += matchCount = plusSign.match(str, index, endIndex);
        }
        boolean isSignificandSigned = matchCount > 0;
        if (index == endIndex) {
            return SYNTAX_ERROR_BITS;
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
            byte ch = str[index];
            int digit = digitSet.toDigit(ch);
            if (digit < 10) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + digit;
            } else if ((matchCount = decimalSeparator.match(str, index, endIndex)) > 0) {
                illegal |= integerDigitCount >= 0;
                decimalSeparatorIndex = index;
                integerDigitCount = index - significandStartIndex - groupingCount;
                index += matchCount - 1;
            } else if ((matchCount = groupingSeparator.match(str, index, endIndex)) > 0) {
                illegal |= decimalSeparatorIndex != -1;
                groupingCount += matchCount;
                index += matchCount - 1;
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

        // Parse optional sign after significand
        // -------------------
        if (index < endIndex && !isSignificandSigned) {
            matchCount = minusSign.match(str, index, endIndex);
            if (matchCount > 0) {
                isNegative = true;
                index += matchCount;
            } else {
                index += plusSign.match(str, index, endIndex);
            }
        }

        // Parse exponent number
        // ---------------------
        int expNumber = 0;
        boolean isExponentSigned = false;
        if (digitCount > 0) {
            int count = exponentSeparator.match(str, index, endIndex);
            if (count > 0) {
                index += count;

                // Parse optional sign before exponent number
                byte ch = charAt(str, index, endIndex);
                boolean isExponentNegative = (matchCount = minusSign.match(str, index, endIndex)) > 0;
                if (isExponentNegative) {
                    index += matchCount;
                } else {
                    index += plusSign.match(str, index, endIndex);
                }
                ch = charAt(str, index, endIndex);
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


                // Parse optional sign after exponent number
                if (!isExponentSigned) {
                    boolean isExponentNegative2 = (matchCount = minusSign.match(str, index, endIndex)) > 0;
                    if (isExponentNegative2 || (matchCount = plusSign.match(str, index, endIndex)) > 0) {
                        isExponentNegative |= isExponentNegative2;
                        index += matchCount;
                    }
                }

                if (isExponentNegative) {
                    expNumber = -expNumber;
                }
                exponent += expNumber;
            }
        }

        // Parse NaN or Infinity (this occurs rarely)
        // ---------------------
        if (!illegal && digitCount == 0) {
            return parseNaNOrInfinity(str, index, endIndex, isNegative, isSignificandSigned);
        }

        // Check if FloatingPointLiteral is complete
        // ------------------------
        if (illegal || index < endIndex) {
            return SYNTAX_ERROR_BITS;
        }

        // Re-parse significand in case of a potential overflow
        // -----------------------------------------------
        final boolean isSignificandTruncated;
        int exponentOfTruncatedSignificand;
        if (digitCount > 19) {
            int truncatedDigitCount = 0;
            significand = 0;
            for (index = significandStartIndex; index < significandEndIndex; index++) {
                byte ch = str[index];
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

    private long parseNaNOrInfinity(byte[] str, int index, int endIndex, boolean isNegative, boolean isSignificandSigned) {
        int nanMatch = nan.match(str, index, endIndex);
        if (nanMatch > 0) {
            index += nanMatch;
            if (index < endIndex && !isSignificandSigned) {
                int matchCount;
                if ((matchCount = minusSign.match(str, index, endIndex)) > 0
                        || (matchCount = plusSign.match(str, index, endIndex)) > 0) {
                    index += matchCount;
                }
            }
            return (index == endIndex) ? nan() : SYNTAX_ERROR_BITS;
        }
        int infinityMatch = infinity.match(str, index, endIndex);
        if (infinityMatch > 0) {
            index += infinityMatch;
            if (index < endIndex && !isSignificandSigned) {
                int matchCount = minusSign.match(str, index, endIndex);
                isNegative = matchCount > 0;
                if (isNegative || (matchCount = plusSign.match(str, index, endIndex)) > 0) {
                    index += matchCount;
                }
            }
            if (index == endIndex) {
                return isNegative ? negativeInfinity() : positiveInfinity();
            }
        }
        return SYNTAX_ERROR_BITS;
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
     * @param startIndex                     the start index of the literal in str
     * @param endIndex                       the end index of the literal in str
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    abstract long valueOfFloatLiteral(byte[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative,
                                      long significand, int exponent, boolean isSignificandTruncated,
                                      int exponentOfTruncatedSignificand, int exponentValue, int startIndex, int endIndex);

    protected double slowPathToDouble(byte[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, int exponentValue) {
        return SlowDoubleConversionPath.toDouble(str, digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue);
    }


}
