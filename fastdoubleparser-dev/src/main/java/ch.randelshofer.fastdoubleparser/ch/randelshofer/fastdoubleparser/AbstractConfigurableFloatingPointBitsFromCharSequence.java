/*
 * @(#)AbstractConfigurableFloatingPointBitsFromCharSequence.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Formattable floating point parser.
 */
abstract class AbstractConfigurableFloatingPointBitsFromCharSequence extends AbstractFloatValueParser {
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

    public AbstractConfigurableFloatingPointBitsFromCharSequence(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator(), ignoreCase);
        this.zeroChar = symbols.zeroDigit();
        this.minusSignChar = CharSet.copyOf(symbols.minusSign(), ignoreCase);
        this.exponentSeparatorChar = CharSet.copyOfFirstChar(symbols.exponentSeparator(), ignoreCase);
        this.exponentSeparatorTrie = CharTrie.of(symbols.exponentSeparator(), ignoreCase);
        this.plusSignChar = CharSet.copyOf(symbols.plusSign(), ignoreCase);
        this.nanTrie = CharTrie.of(symbols.nan(), ignoreCase);
        this.infinityTrie = CharTrie.of(symbols.infinity(), ignoreCase);
        Set<Character> nanOrInfinitySet = new LinkedHashSet<>();
        for (String s : symbols.nan()) {
            nanOrInfinitySet.add(s.charAt(0));
        }
        for (String s : symbols.infinity()) {
            nanOrInfinitySet.add(s.charAt(0));
        }
        nanOrInfinityChar = CharSet.copyOf(nanOrInfinitySet, ignoreCase);
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
    public final long parseFloatingPointLiteral(CharSequence str, int offset, int length) {
        final int endIndex = checkBounds(str.length(), offset, length);

        // Skip leading whitespace
        // -------------------
        int index = skipFormatCharacters(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        char ch = str.charAt(index);

        // Parse optional sign
        // -------------------
        final boolean isNegative = isMinusSign(ch);
        if (isNegative || isPlusSign(ch)) {
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

        // Parse significand
        // -----------------
        // Note: a multiplication by a constant is cheaper than an
        //       arbitrary integer multiplication.
        long significand = 0;// significand is treated as an unsigned long
        final int significandStartIndex = index;
        int integerDigitCount = -1;
        int groupingCount = 0;
        boolean illegal = false;

        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            int digit = (char) (ch - zeroChar);
            if (digit < 10) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + digit;
            } else if (isDecimalSeparator(ch)) {
                illegal |= integerDigitCount >= 0;
                integerDigitCount = index - significandStartIndex - groupingCount;
            } else if (isGroupingSeparator(ch)) {
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
            exponent = 0;
        } else {
            digitCount = significandEndIndex - significandStartIndex - 1 - groupingCount;
            exponent = integerDigitCount - digitCount;
        }

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

        // Check if FloatingPointLiteral is complete
        // ------------------------
        if (illegal || index < endIndex
                || !hasLeadingZero && digitCount == 0) {
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
                ch = str.charAt(index);
                int digit = (char) (ch - zeroChar);
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
        return valueOfFloatLiteral(str, offset, endIndex, isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
    }

    private boolean isMinusSign(char c) {
        return minusSignChar.contains(c);
    }

    private boolean isPlusSign(char c) {
        return plusSignChar.contains(c);
    }


    protected CharSequence filterInputString(CharSequence str, int startIndex, int endIndex) {
        StringBuilder buf = new StringBuilder(endIndex - startIndex);

        // Filter leading format characters
        // -------------------
        int index = skipFormatCharacters(str, startIndex, endIndex);
        char ch = str.charAt(index);

        // Filter optional sign
        // -------------------
        final boolean isNegative = isMinusSign(ch);
        if (isNegative) buf.append('-');
        if (isNegative || isPlusSign(ch)) {
            ++index;
        }

        // We do not need to parse NaN or Infinity, this case has already been processed

        // Parse significand
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            int digit = (char) (ch - zeroChar);
            if (digit < 10) {
                buf.append((char) (digit + '0'));
            } else if (isDecimalSeparator(ch)) {
                buf.append('.');
            } else if (!isGroupingSeparator(ch)) {
                break;
            }

        }

        // Parse exponent number
        // ---------------------
        int count = exponentSeparatorTrie.match(str, index, endIndex);
        if (count > 0) {
            buf.append('e');
            index += count;
            index = skipFormatCharacters(str, index, endIndex);
            ch = charAt(str, index, endIndex);
            boolean isExponentNegative = isMinusSign(ch);
            if (isExponentNegative) {
                buf.append('-');
            }
            if (isExponentNegative || isPlusSign(ch)) {
                ++index;
            }
            ch = str.charAt(index);
            int digit = (char) (ch - zeroChar);
            do {
                buf.append((char) (digit + '0'));
                ch = charAt(str, ++index, endIndex);
                digit = (char) (ch - zeroChar);
            } while (digit < 10);
        }


        return buf;
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

    private long parseNaNOrInfinity(CharSequence str, int index, int endIndex, boolean isNegative) {
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
            CharSequence str, int startIndex, int endIndex,
            boolean isNegative, long significand, int exponent,
            boolean isSignificandTruncated, int exponentOfTruncatedSignificand);

}
