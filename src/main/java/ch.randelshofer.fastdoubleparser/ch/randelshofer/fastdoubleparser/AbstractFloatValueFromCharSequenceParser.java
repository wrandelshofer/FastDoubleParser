/*
 * @(#)AbstractFloatValueFromCharSequenceParser.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Abstract base class for parsers that parse a float value from a {@link CharSequence}.
 */
public abstract class AbstractFloatValueFromCharSequenceParser extends AbstractFloatValueParser {

    protected NumberFormatException newNumberFormatException(CharSequence str, int startIndex, int endIndex) {
        if (str.length() > 1024) {
            // str can be up to Integer.MAX_VALUE characters long
            return new NumberFormatException("For input string of length " + str.length());
        } else {
            return new NumberFormatException("For input string: \"" + str.subSequence(startIndex, endIndex) + "\"");
        }
    }

    protected int skipWhitespace(CharSequence str, int startIndex, int endIndex) {
        int index = startIndex;
        for (; index < endIndex; index++) {
            if (str.charAt(index) > 0x20) {
                break;
            }
        }
        return index;
    }

    /**
     * Parses the {@code FloatValue} production.
     * <blockquote>
     * <dl>
     *  <dt><i>FloatValue:</i></dt>
     *  <dd><i>[Sign] DecimalFloatingPointLiteral</i></dd>
     * </dl>
     * </blockquote>
     * See {@link ch.randelshofer.fastdoubleparser} for the complete production.
     *
     * @param str    a string
     * @param offset start offset of the FloatValue in str
     * @param length lenght of FloatValue in str
     * @return the parsed value
     * @throws NumberFormatException if parsing failed
     */
    public long parseFloatValue(CharSequence str, int offset, int length) throws NumberFormatException {
        final int endIndex = offset + length;

        // Skip leading whitespace
        // -------------------
        int index = skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException("empty String");
        }
        char ch = str.charAt(index);

        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            if (ch == 0) {
                throw newNumberFormatException(str, offset, endIndex);
            }
        }

        // Parse NaN or Infinity
        // ---------------------
        if (ch == 'N') {
            return parseNaN(str, index, endIndex);
        } else if (ch == 'I') {
            return parseInfinity(str, index, endIndex, isNegative);
        }

        // Parse optional leading zero
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            if (ch == 'x' || ch == 'X') {
                return parseRestOfHexFloatingPointLiteral(str, index + 1, offset, endIndex, isNegative);
            }
        }

        return parseRestOfDecimalFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
    }

    protected long parseInfinity(CharSequence str, int startIndex, int endIndex, boolean negative) {
        if (startIndex + 7 < endIndex
                //  && str.charAt(index) == 'I'
                && str.charAt(startIndex + 1) == 'n'
                && str.charAt(startIndex + 2) == 'f'
                && str.charAt(startIndex + 3) == 'i'
                && str.charAt(startIndex + 4) == 'n'
                && str.charAt(startIndex + 5) == 'i'
                && str.charAt(startIndex + 6) == 't'
                && str.charAt(startIndex + 7) == 'y'
        ) {
            startIndex = skipWhitespace(str, startIndex + 8, endIndex);
            if (startIndex < endIndex) {
                throw newNumberFormatException(str, startIndex, endIndex);
            }
            return negative ? negativeInfinity() : positiveInfinity();
        } else {
            throw newNumberFormatException(str, startIndex, endIndex);
        }
    }

    protected abstract long negativeInfinity();

    protected abstract long positiveInfinity();

    protected abstract long nan();

    protected long parseNaN(CharSequence str, int startIndex, int endIndex) {
        if (startIndex + 2 < endIndex
                //   && str.charAt(index) == 'N'
                && str.charAt(startIndex + 1) == 'a'
                && str.charAt(startIndex + 2) == 'N') {

            startIndex = skipWhitespace(str, startIndex + 3, endIndex);
            if (startIndex < endIndex) {
                throw newNumberFormatException(str, startIndex, endIndex);
            }

            return nan();
        } else {
            throw newNumberFormatException(str, startIndex, endIndex);
        }
    }

    /**
     * Parses the following rules
     * (more rules are defined in {@link AbstractFloatValueParser}):
     * <dl>
     * <dt><i>RestOfDecimalFloatingPointLiteral</i>:
     * <dd><i>[Digits] {@code .} [Digits] [DecExponent]</i>
     * <dd><i>{@code .} Digits [DecExponent]</i>
     * <dd><i>[Digits] DecExponent</i>
     * </dl>
     *
     * @param str            the input string
     * @param index          index to the first character of RestOfHexFloatingPointLiteral
     * @param startIndex     the start index of h
     * @param endIndex       the end index of the string
     * @param isNegative     if the resulting number is negative
     * @param hasLeadingZero if the digit '0' has been consumed
     * @return the parsed value wrapped in a long
     */
    protected long parseRestOfDecimalFloatLiteral(CharSequence str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        // Parse DecSignificand production, after we have already parsed
        // the optional leading zero.
        // ------------
        // Note: a multiplication by a constant is cheaper than an
        //       arbitrary integer multiplication.
        long significand = 0;// significand is treated as an unsigned long
        int exponent = 0;
        final int indexOfFirstDigit = index;
        int virtualIndexOfPoint = -1;
        char ch = 0;
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            if (isDigit(ch)) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + ch - '0';
            } else if (ch == '.') {
                if (virtualIndexOfPoint != -1) {
                    throw newNumberFormatException(str, startIndex, endIndex);
                }
                virtualIndexOfPoint = index;
                while (index < endIndex - 8) {
                    long parsed = tryToParseEightDigits(str, index + 1);
                    if (parsed >= 0) {
                        // This might overflow, we deal with it later.
                        significand = significand * 100_000_000L + parsed;
                        index += 8;
                    } else {
                        break;
                    }
                }

            } else {
                break;
            }
        }
        final int digitCount;
        final int indexAfterDigits = index;
        if (virtualIndexOfPoint == -1) {
            digitCount = indexAfterDigits - indexOfFirstDigit;
            virtualIndexOfPoint = indexAfterDigits;
        } else {
            digitCount = indexAfterDigits - indexOfFirstDigit - 1;
            exponent = virtualIndexOfPoint - index + 1;
        }

        // Parse exponent number
        // ---------------------
        int expNumber = 0;
        final boolean hasExponent = (ch == 'e') || (ch == 'E');
        if (hasExponent) {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            boolean neg_exp = ch == '-';
            if (neg_exp || ch == '+') {
                ch = ++index < endIndex ? str.charAt(index) : 0;
            }
            if (!isDigit(ch)) {
                throw newNumberFormatException(str, startIndex, endIndex);
            }
            do {
                // Guard against overflow of exp_number
                if (expNumber < AbstractFloatValueParser.MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * expNumber + ch - '0';
                }
                ch = ++index < endIndex ? str.charAt(index) : 0;
            } while (isDigit(ch));
            if (neg_exp) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }

        // Skip trailing whitespace
        // ------------------------
        index = skipWhitespace(str, index, endIndex);
        if (index < endIndex
                || !hasLeadingZero && digitCount == 0 && str.charAt(virtualIndexOfPoint) != '.') {
            throw newNumberFormatException(str, startIndex, endIndex);
        }

        // Re-parse digits in case of a potential overflow
        // -----------------------------------------------
        final boolean isDigitsTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point
        if (digitCount > 19) {
            significand = 0;
            for (index = indexOfFirstDigit; index < indexAfterDigits; index++) {
                ch = str.charAt(index);
                if (ch == '.') {
                    skipCountInTruncatedDigits++;
                } else {
                    if (Long.compareUnsigned(significand, AbstractFloatValueParser.MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
                        significand = 10 * significand + ch - '0';
                    } else {
                        break;
                    }
                }
            }
            isDigitsTruncated = (index < indexAfterDigits);
        } else {
            isDigitsTruncated = false;
        }
        return convertFloatLiteralToValue(str, startIndex, endIndex, isNegative, significand, exponent, isDigitsTruncated,
                virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber);
    }

    protected abstract long convertFloatLiteralToValue(CharSequence str, int startIndex, int endIndex, boolean isNegative, long digits, int exponent, boolean isDigitsTruncated,
                                                       int exponentOfTruncatedValue);

    protected long tryToParseEightDigits(CharSequence str, int offset) {
        // Performance: We extract the chars in two steps so that we
        //              can benefit from out of order execution in the CPU.
        long first = str.charAt(offset)
                | (long) str.charAt(offset + 1) << 16
                | (long) str.charAt(offset + 2) << 32
                | (long) str.charAt(offset + 3) << 48;

        long second = str.charAt(offset + 4)
                | (long) str.charAt(offset + 5) << 16
                | (long) str.charAt(offset + 6) << 32
                | (long) str.charAt(offset + 7) << 48;

        return FastDoubleSimd.tryToParseEightDigitsUtf16Vector(first, second);
    }


    /**
     * Parses the following rules
     * (more rules are defined in {@link AbstractFloatValueParser}):
     * <dl>
     * <dt><i>RestOfHexFloatingPointLiteral</i>:
     * <dd><i>RestOfHexSignificand BinaryExponent</i>
     * </dl>
     *
     * <dl>
     * <dt><i>RestOfHexSignificand:</i>
     * <dd><i>HexDigits</i>
     * <dd><i>HexDigits</i> {@code .}
     * <dd><i>[HexDigits]</i> {@code .} <i>HexDigits</i>
     * </dl>
     *
     * @param str        the input string
     * @param index      index to the first character of RestOfHexFloatingPointLiteral
     * @param startIndex the start index of the string
     * @param endIndex   the end index of the string
     * @param isNegative if the resulting number is negative
     * @return a double representation
     */
    protected long parseRestOfHexFloatingPointLiteral(
            CharSequence str, int index, int startIndex, int endIndex, boolean isNegative) {

        // Parse mantissa
        // ------------
        long digits = 0;// digits is treated as an unsigned long
        int exponent = 0;
        final int indexOfFirstDigit = index;
        int virtualIndexOfPoint = -1;
        final int digitCount;
        char ch = 0;
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            // Table look up is faster than a sequence of if-else-branches.
            int hexValue = ch > 127 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch];
            if (hexValue >= 0) {
                digits = (digits << 4) | hexValue;// This might overflow, we deal with it later.
            } else if (hexValue == AbstractFloatValueParser.DECIMAL_POINT_CLASS) {
                if (virtualIndexOfPoint != -1) {
                    throw newNumberFormatException(str, startIndex, endIndex);
                }
                virtualIndexOfPoint = index;
                /*
                while (index < endIndex - 8) {
                    long parsed = tryToParseEightHexDigits(str, index + 1);
                    if (parsed >= 0) {
                        // This might overflow, we deal with it later.
                        digits = (digits << 32) + parsed;
                        index += 8;
                    } else {
                        break;
                    }
                }
                */
            } else {
                break;
            }
        }
        final int indexAfterDigits = index;
        if (virtualIndexOfPoint == -1) {
            digitCount = indexAfterDigits - indexOfFirstDigit;
            virtualIndexOfPoint = indexAfterDigits;
        } else {
            digitCount = indexAfterDigits - indexOfFirstDigit - 1;
            exponent = Math.min(virtualIndexOfPoint - index + 1, AbstractFloatValueParser.MAX_EXPONENT_NUMBER) * 4;
        }

        // Parse exponent number
        // ---------------------
        long exp_number = 0;
        final boolean hasExponent = (ch == 'p') || (ch == 'P');
        if (hasExponent) {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            boolean neg_exp = ch == '-';
            if (neg_exp || ch == '+') {
                ch = ++index < endIndex ? str.charAt(index) : 0;
            }
            if (!isDigit(ch)) {
                throw newNumberFormatException(str, startIndex, endIndex);
            }
            do {
                // Guard against overflow of exp_number
                if (exp_number < AbstractFloatValueParser.MAX_EXPONENT_NUMBER) {
                    exp_number = 10 * exp_number + ch - '0';
                }
                ch = ++index < endIndex ? str.charAt(index) : 0;
            } while (isDigit(ch));
            if (neg_exp) {
                exp_number = -exp_number;
            }
            exponent += exp_number;
        }

        // Skip trailing whitespace
        // ------------------------
        index = skipWhitespace(str, index, endIndex);
        if (index < endIndex
                || digitCount == 0 && str.charAt(virtualIndexOfPoint) != '.'
                || !hasExponent) {
            throw newNumberFormatException(str, startIndex, endIndex);
        }

        // Re-parse digits in case of a potential overflow
        // -----------------------------------------------
        final boolean isDigitsTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point
        if (digitCount > 16) {
            digits = 0;
            for (index = indexOfFirstDigit; index < indexAfterDigits; index++) {
                ch = str.charAt(index);
                // Table look up is faster than a sequence of if-else-branches.
                int hexValue = ch > 127 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch];
                if (hexValue >= 0) {
                    if (Long.compareUnsigned(digits, AbstractFloatValueParser.MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
                        digits = (digits << 4) | hexValue;
                    } else {
                        break;
                    }
                } else {
                    skipCountInTruncatedDigits++;
                }
            }
            isDigitsTruncated = (index < indexAfterDigits);
        } else {
            isDigitsTruncated = false;
        }

        return convertHexFloatLiteralToValue(index, isNegative, digits, exponent, virtualIndexOfPoint, exp_number, isDigitsTruncated, skipCountInTruncatedDigits, str);
    }

    protected abstract long convertHexFloatLiteralToValue(int index, boolean isNegative, long digits, long exponent, int virtualIndexOfPoint, long exp_number, boolean isDigitsTruncated, int skipCountInTruncatedDigits, CharSequence str);
}
