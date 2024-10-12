/*
 * @(#)AbstractJavaFloatingPointBitsFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Lenient floating point parser.
 */
abstract class AbstractLenientFloatingPointBitsFromString extends AbstractFloatValueParser {
    /* This value is used for undefined character symbols. This value must be outside Character.MIN_VALUE, Character.MAX_VALUE */
    private final static int UNDEFINED_CHAR = Integer.MIN_VALUE;
    /* This value is used for EOF symbols. This value must be outside Character.MIN_VALUE, Character.MAX_VALUE */
    private final static char EOF_CHAR = 0;
    private final char zeroChar;
    private final CharSet minusSignChar;
    private final CharSet plusSignChar;
    private final CharSet decimalSeparator;
    private final CharSet groupingSeparator;
    private final CharSet nanOrInfinityChar;
    private final CharTrie nanTrie;
    private final CharTrie infinityTrie;
    private final CharSet exponentSeparatorChar;
    private final CharTrie exponentSeparatorTrie;

    public AbstractLenientFloatingPointBitsFromString(NumberFormatSymbols symbols) {
        this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator());
        this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator());
        this.zeroChar = symbols.zeroDigit();
        this.minusSignChar = CharSet.copyOf(symbols.minusSign());
        this.exponentSeparatorChar = CharSet.copyOfFirstChar(symbols.exponentSeparator());
        this.exponentSeparatorTrie = new CharTrie(symbols.exponentSeparator());
        this.plusSignChar = CharSet.copyOf(symbols.plusSign());
        this.nanTrie = new CharTrie(symbols.nan());
        this.infinityTrie = new CharTrie(symbols.infinity());
        Set<Character> nanOrInfinitySet = new LinkedHashSet<>();
        for (String s : symbols.nan()) {
            nanOrInfinitySet.add(s.charAt(0));
        }
        for (String s : symbols.infinity()) {
            nanOrInfinitySet.add(s.charAt(0));
        }
        nanOrInfinityChar = CharSet.copyOf(nanOrInfinitySet);
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
     * Skips to first digit.
     *
     * @param str      a string
     * @param index    start index (inclusive) of the optional white space
     * @param endIndex end index (exclusive) of the optional white space
     * @return index after the optional white space
     */
    private int skipToFirstDigit(String str, int index, int endIndex) {
        while (index < endIndex && ((char) (str.charAt(index) - zeroChar)) > 9) {
            index++;
        }
        return index;
    }

    private static int skipWhitespace(String str, int index, int endIndex) {
        while (index < endIndex && str.charAt(index) <= ' ') {
            index++;
        }
        return index;
    }

    /**
     * Parses a {@code DecimalFloatingPointLiteral} production with optional
     * trailing white space until the end of the text.
     * Given that we have already consumed the optional leading zero of
     * the {@code DecSignificand}.
     * <blockquote>
     * <dl>
     * <dt><i>DecimalFloatingPointLiteralWithWhiteSpace:</i></dt>
     * <dd><i>DecimalFloatingPointLiteral [WhiteSpace] EOT</i></dd>
     * </dl>
     * </blockquote>
     * See {@link JavaDoubleParser} for the grammar of
     * {@code DecimalFloatingPointLiteral} and {@code DecSignificand}.
     *
     * @param str            a string
     * @param index          the current index
     * @param startIndex     start index inclusive of the {@code DecimalFloatingPointLiteralWithWhiteSpace}
     * @param endIndex       end index (exclusive)
     * @param isNegative     true if the float value is negative
     * @param hasLeadingZero true if we have consumed the optional leading zero
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    private long parseDecFloatLiteral(String str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        // Parse significand
        // -----------------
        // Note: a multiplication by a constant is cheaper than an
        //       arbitrary integer multiplication.
        long significand = 0;// significand is treated as an unsigned long
        final int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch = 0;
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            int digit = (char) (ch - zeroChar);
            if (digit < 10) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + digit;
            } else if (isDecimalSeparator(ch)) {
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
            } else if (!isGroupingSeparator(ch)) {
                break;
            }
        }
        final int digitCount;
        final int significandEndIndex = index;
        int exponent;
        if (virtualIndexOfPoint < 0) {
            digitCount = significandEndIndex - significandStartIndex;
            virtualIndexOfPoint = significandEndIndex;
            exponent = 0;
        } else {
            digitCount = significandEndIndex - significandStartIndex - 1;
            exponent = virtualIndexOfPoint - significandEndIndex + 1;
        }

        // Parse exponent number
        // ---------------------
        int expNumber = 0;
        if (isExponentSeparator(ch)) {
            int count = exponentSeparatorTrie.matchBranchless(str, index, endIndex);
            illegal |= count == 0;
            index += count;
            ch = charAt(str, index, endIndex);
            boolean isExponentNegative = isMinusSign(ch);
            if (isExponentNegative || isPlusSign(ch)) {
                ch = charAt(str, ++index, endIndex);
            }
            int digit = (char) (ch - zeroChar);
            illegal |= digit >= 10;
            do {
                // Guard against overflow
                if (expNumber < AbstractFloatValueParser.MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * expNumber + digit;
                }
                ch = charAt(str, ++index, endIndex);
                digit = (char) (ch - zeroChar);
            } while (digit < 10);
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }

        // Skip trailing whitespace and check if FloatingPointLiteral is complete
        // ------------------------
        index = skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex
                || !hasLeadingZero && digitCount == 0) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }

        // Re-parse significand in case of a potential overflow
        // -----------------------------------------------
        final boolean isSignificandTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point or over a grouping separator
        int exponentOfTruncatedSignificand;
        if (digitCount > 19) {
            significand = 0;
            for (index = significandStartIndex; index < significandEndIndex; index++) {
                ch = str.charAt(index);
                if (isDecimalSeparator(ch) || isGroupingSeparator(ch)) {
                    skipCountInTruncatedDigits++;
                } else {
                    if (Long.compareUnsigned(significand, AbstractFloatValueParser.MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
                        significand = 10 * significand + ch - zeroChar;
                    } else {
                        break;
                    }
                }
            }
            isSignificandTruncated = index < significandEndIndex;
            exponentOfTruncatedSignificand = virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return valueOfFloatLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
    }

    private boolean isDecimalSeparator(char ch) {
        return decimalSeparator.contains(ch);
    }

    private boolean isGroupingSeparator(char ch) {
        return groupingSeparator.contains(ch);
    }

    private boolean isExponentSeparator(char ch) {
        return exponentSeparatorChar.contains(ch);
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
    public final long parseFloatingPointLiteral(String str, int offset, int length) {
        final int endIndex = checkBounds(str.length(), offset, length);

        // Skip leading whitespace
        // -------------------
        int index = skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        char ch = str.charAt(index);

        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = charAt(str, ++index, endIndex);
            if (ch == 0) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        // Parse NaN or Infinity (this occurs rarely)
        // ---------------------
        if (nanOrInfinityChar.contains(ch)) {
            return parseNaNOrInfinity(str, index, endIndex, isNegative);
        }

        // Parse optional leading zero
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';

        // Parse the rest
        // --------------
        return parseDecFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
    }

    private boolean isMinusSign(char c) {
        return minusSignChar.contains(c);
    }

    private boolean isPlusSign(char c) {
        return plusSignChar.contains(c);
    }


    protected StringBuilder filterInputString(String str, int startIndex, int endIndex) {
        StringBuilder b = new StringBuilder(endIndex - startIndex);
        for (int i = startIndex; i < endIndex; i++) {
            char ch = str.charAt(i);
            int digit = (char) (ch - zeroChar);
            if (digit < 10) {
                b.append((char) (digit + '0'));
            } else if (isMinusSign(ch)) {
                b.append('-');
            } else if (isDecimalSeparator(ch)) {
                b.append('.');
            } else if (isExponentSeparator(ch)) {
                b.append('e');
            }
        }
        return b;
    }

    private long parseNaNOrInfinity(String str, int index, int endIndex, boolean isNegative) {
        int nanMatch = nanTrie.matchBranchless(str, index, endIndex);
        if (nanMatch > 0) {
            if (skipWhitespace(str, index + nanMatch, endIndex) == endIndex) {
                return nan();
            }
        }
        int infinityMatch = infinityTrie.matchBranchless(str, index, endIndex);
        if (infinityMatch > 0) {
            if (skipWhitespace(str, index + infinityMatch, endIndex) == endIndex) {
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
     * @param startIndex                     the start index (inclusive) of the float literal
     *                                       inside the string
     * @param endIndex                       the end index (exclusive) of the float literal inside
     *                                       the string
     * @param isNegative                     whether the float value is negative
     * @param significand                    the significand of the float value (can be truncated)
     * @param exponent                       the exponent of the float value
     * @param isSignificandTruncated         whether the significand is truncated
     * @param exponentOfTruncatedSignificand the exponent value of the truncated
     *                                       significand
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    abstract long valueOfFloatLiteral(
            String str, int startIndex, int endIndex,
            boolean isNegative, long significand, int exponent,
            boolean isSignificandTruncated, int exponentOfTruncatedSignificand);

    /**
     * Computes a float value from the given components of a hexadecimal float
     * literal.
     *
     * @param str                            the string that contains the float literal (and maybe more)
     * @param startIndex                     the start index (inclusive) of the float literal
     *                                       inside the string
     * @param endIndex                       the end index (exclusive) of the float literal inside
     *                                       the string
     * @param isNegative                     whether the float value is negative
     * @param significand                    the significand of the float value (can be truncated)
     * @param exponent                       the exponent of the float value
     * @param isSignificandTruncated         whether the significand is truncated
     * @param exponentOfTruncatedSignificand the exponent value of the truncated
     *                                       significand
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    abstract long valueOfHexLiteral(
            String str, int startIndex, int endIndex,
            boolean isNegative, long significand, int exponent,
            boolean isSignificandTruncated, int exponentOfTruncatedSignificand);
}
