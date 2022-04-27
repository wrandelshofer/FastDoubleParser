/*
 * @(#)DoubleFromCharArrayParser.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import static ch.randelshofer.fastdoubleparser.AbstractFloatValueParser.CHAR_TO_HEX_MAP;
import static ch.randelshofer.fastdoubleparser.AbstractFloatValueParser.DECIMAL_POINT_CLASS;
import static ch.randelshofer.fastdoubleparser.AbstractFloatValueParser.MAX_EXPONENT_NUMBER;
import static ch.randelshofer.fastdoubleparser.AbstractFloatValueParser.MINIMAL_NINETEEN_DIGIT_INTEGER;
import static ch.randelshofer.fastdoubleparser.AbstractFloatValueParser.OTHER_CLASS;

/**
 * Parses a double from a char array.
 */
public class DoubleFromCharArrayParser {

    public DoubleFromCharArrayParser() {

    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private NumberFormatException newNumberFormatException(char[] str, int off, int len) {
        if (len > 1024) {
            // str can be up to Integer.MAX_VALUE characters long
            return new NumberFormatException("For input string of length " + len);
        } else {
            return new NumberFormatException("For input string: \"" + new String(str, off, len) + "\"");
        }
    }

    /**
     * Convenience method for writing
     * {@code #parseDouble(str.toCharArray()}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     * @see #parseDouble(char[], int, int)
     */
    public double parseDouble(String str) throws NumberFormatException {
        return parseDouble(str.toCharArray());
    }

    /**
     * Convenience method for calling {@link #parseDouble(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public double parseDouble(char[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@link char} array and converts it
     * into a {@code double} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @param off The index of the first character to parse
     * @param len The number of characters to parse
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public double parseDouble(char[] str, int off, int len) throws NumberFormatException {
        final int endIndex = len + off;

        // Skip leading whitespace
        // -------------------
        int index = skipWhitespace(str, off, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException("empty String");
        }
        char ch = str[index];

        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str[index] : 0;
            if (ch == 0) {
                throw newNumberFormatException(str, off, len);
            }
        }

        // Parse NaN or Infinity
        // ---------------------
        if (ch == 'N') {
            return parseNaN(str, index, endIndex, off);
        } else if (ch == 'I') {
            return parseInfinity(str, index, endIndex, isNegative, off);
        }

        // Parse optional leading zero
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            ch = ++index < endIndex ? str[index] : 0;
            if (ch == 'x' || ch == 'X') {
                return parseRestOfHexFloatingPointLiteral(str, index + 1, off, endIndex, isNegative);
            }
        }

        return parseRestOfDecimalFloatLiteral(str, index, off, endIndex, isNegative, hasLeadingZero);
    }

    private double parseInfinity(char[] str, int index, int endIndex, boolean negative, int off) {
        if (index + 7 < endIndex
                //  && str.charAt(index) == 'I'
                && str[index + 1] == 'n'
                && str[index + 2] == 'f'
                && str[index + 3] == 'i'
                && str[index + 4] == 'n'
                && str[index + 5] == 'i'
                && str[index + 6] == 't'
                && str[index + 7] == 'y'
        ) {
            index = skipWhitespace(str, index + 8, endIndex);
            if (index < endIndex) {
                throw newNumberFormatException(str, off, endIndex - off);
            }
            return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        } else {
            throw newNumberFormatException(str, off, endIndex - off);
        }
    }

    private double parseNaN(char[] str, int index, int endIndex, int off) {
        if (index + 2 < endIndex
                //   && str.charAt(index) == 'N'
                && str[index + 1] == 'a'
                && str[index + 2] == 'N') {

            index = skipWhitespace(str, index + 3, endIndex);
            if (index < endIndex) {
                throw newNumberFormatException(str, off, endIndex - off);
            }

            return Double.NaN;
        } else {
            throw newNumberFormatException(str, off, endIndex - off);
        }
    }

    /**
     * Parses the following rules
     * (more rules are defined in {@link #parseDouble}):
     * <dl>
     * <dt><i>RestOfDecimalFloatingPointLiteral</i>:
     * <dd><i>[Digits] {@code .} [Digits] [DecExponent]</i>
     * <dd><i>{@code .} Digits [DecExponent]</i>
     * <dd><i>[Digits] DecExponent</i>
     * </dl>
     *
     * @param str            the input string
     * @param index          index to the first character of RestOfHexFloatingPointLiteral
     * @param endIndex       the end index of the string
     * @param isNegative     if the resulting number is negative
     * @param hasLeadingZero if the digit '0' has been consumed
     * @return a double representation
     */
    private double parseRestOfDecimalFloatLiteral(char[] str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        // Parse the production: Digits . [Digits] ;
        // ------------
        // Note: a multiplication by a constant is cheaper than an
        //       arbitrary integer multiplication.
        long mantissa = 0;// digits is treated as an unsigned long
        int exponent = 0;
        final int indexOfFirstDigit = index;
        int virtualIndexOfPoint = -1;
        final int digitCount;
        char ch = 0;
        for (; index < endIndex; index++) {
            ch = str[index];
            if (isDigit(ch)) {
                // This might overflow, we deal with it later.
                mantissa = 10 * mantissa + ch - '0';
            } else if (ch == '.') {
                if (virtualIndexOfPoint != -1) {
                    throw newNumberFormatException(str, startIndex, endIndex - startIndex);
                }
                virtualIndexOfPoint = index;
                while (index < endIndex - 8) {
                    long parsed = FastDoubleSimd.tryToParseEightDigitsUtf16Vector(str, index + 1);
                    if (parsed >= 0) {
                        // This might overflow, we deal with it later.
                        mantissa = mantissa * 100_000_000L + parsed;
                        index += 8;
                    } else {
                        break;
                    }
                }

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
            exponent = virtualIndexOfPoint - index + 1;
        }

        // Parse exponent number
        // ---------------------
        int expNumber = 0;
        final boolean hasExponent = (ch == 'e') || (ch == 'E');
        if (hasExponent) {
            ch = ++index < endIndex ? str[index] : 0;
            boolean neg_exp = ch == '-';
            if (neg_exp || ch == '+') {
                ch = ++index < endIndex ? str[index] : 0;
            }
            if (!isDigit(ch)) {
                throw newNumberFormatException(str, startIndex, endIndex - startIndex);
            }
            do {
                // Guard against overflow of exp_number
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * expNumber + ch - '0';
                }
                ch = ++index < endIndex ? str[index] : 0;
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
                || !hasLeadingZero && digitCount == 0 && str[virtualIndexOfPoint] != '.') {
            throw newNumberFormatException(str, startIndex, endIndex - startIndex);
        }

        // Re-parse digits in case of a potential overflow
        // -----------------------------------------------
        final boolean isDigitsTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point
        if (digitCount > 19) {
            mantissa = 0;
            for (index = indexOfFirstDigit; index < indexAfterDigits; index++) {
                ch = str[index];
                if (ch == '.') {
                    skipCountInTruncatedDigits++;
                } else {
                    if (Long.compareUnsigned(mantissa, MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
                        mantissa = 10 * mantissa + ch - '0';
                    } else {
                        break;
                    }
                }
            }
            isDigitsTruncated = index < indexAfterDigits;
        } else {
            isDigitsTruncated = false;
        }

        double result = FastDoubleMath.decFloatLiteralToDouble(isNegative, mantissa, exponent, isDigitsTruncated,
                virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber);
        return Double.isNaN(result) ? parseRestOfDecimalFloatLiteralTheHardWay(str, startIndex, endIndex - startIndex) : result;
    }

    /**
     * Parses the following rules
     * (more rules are defined in {@link #parseDouble}):
     * <dl>
     * <dt><i>RestOfDecimalFloatingPointLiteral</i>:
     * <dd><i>[Digits] {@code .} [Digits] [DecExponent]</i>
     * <dd><i>{@code .} Digits [DecExponent]</i>
     * <dd><i>[Digits] DecExponent</i>
     * </dl>
     *  @param str            the input string
     */
    private double parseRestOfDecimalFloatLiteralTheHardWay(char[] str, int off, int len) {
        return Double.parseDouble(new String(str, off, len));
    }

    /**
     * Parses the following rules
     * (more rules are defined in {@link #parseDouble}):
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
    private double parseRestOfHexFloatingPointLiteral(
            char[] str, int index, int startIndex, int endIndex, boolean isNegative) {
        if (index >= endIndex) {
            throw newNumberFormatException(str, startIndex, endIndex - startIndex);
        }

        // Parse mantissa
        // ------------
        long digits = 0;// digits is treated as an unsigned long
        int exponent = 0;
        final int indexOfFirstDigit = index;
        int virtualIndexOfPoint = -1;
        final int digitCount;
        char ch = 0;
        for (; index < endIndex; index++) {
            ch = str[index];
            // Table look up is faster than a sequence of if-else-branches.
            int hexValue = ch > 127 ? OTHER_CLASS : CHAR_TO_HEX_MAP[ch];
            if (hexValue >= 0) {
                digits = (digits << 4) | hexValue;// This might overflow, we deal with it later.
            } else if (hexValue == DECIMAL_POINT_CLASS) {
                if (virtualIndexOfPoint != -1) {
                    throw newNumberFormatException(str, startIndex, endIndex - startIndex);
                }
                virtualIndexOfPoint = index;
                /*
                while (index < endIndex - 8) {
                    long parsed = FastDoubleSimd.tryToParseEightHexDigitsUtf16Vector(str, index + 1);
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
            exponent = Math.min(virtualIndexOfPoint - index + 1, MAX_EXPONENT_NUMBER) * 4;
        }

        // Parse exponent number
        // ---------------------
        long exp_number = 0;
        final boolean hasExponent = (ch == 'p') || (ch == 'P');
        if (hasExponent) {
            ch = ++index < endIndex ? str[index] : 0;
            boolean neg_exp = ch == '-';
            if (neg_exp || ch == '+') {
                ch = ++index < endIndex ? str[index] : 0;
            }
            if (!isDigit(ch)) {
                throw newNumberFormatException(str, startIndex, endIndex - startIndex);
            }
            do {
                // Guard against overflow of exp_number
                if (exp_number < MAX_EXPONENT_NUMBER) {
                    exp_number = 10 * exp_number + ch - '0';
                }
                ch = ++index < endIndex ? str[index] : 0;
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
                || digitCount == 0 && str[virtualIndexOfPoint] != '.'
                || !hasExponent) {
            throw newNumberFormatException(str, startIndex, endIndex - startIndex);
        }

        // Re-parse digits in case of a potential overflow
        // -----------------------------------------------
        final boolean isDigitsTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point
        if (digitCount > 16) {
            digits = 0;
            for (index = indexOfFirstDigit; index < indexAfterDigits; index++) {
                ch = str[index];
                // Table look up is faster than a sequence of if-else-branches.
                int hexValue = ch > 127 ? OTHER_CLASS : CHAR_TO_HEX_MAP[ch];
                if (hexValue >= 0) {
                    if (Long.compareUnsigned(digits, MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
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

        double d = FastDoubleMath.hexFloatLiteralToDouble(isNegative, digits, exponent, isDigitsTruncated,
                (virtualIndexOfPoint - index + skipCountInTruncatedDigits) * 4L + exp_number);
        return Double.isNaN(d) ? Double.parseDouble(new String(str, startIndex, endIndex - startIndex)) : d;
    }

    private int skipWhitespace(char[] str, int startIndex, int endIndex) {
        int index = startIndex;
        for (; index < endIndex; index++) {
            if (str[index] > ' ') {
                break;
            }
        }
        return index;
    }


}