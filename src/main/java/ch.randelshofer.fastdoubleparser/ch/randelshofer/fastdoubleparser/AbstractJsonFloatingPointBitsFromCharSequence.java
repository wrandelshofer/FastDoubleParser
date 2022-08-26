/*
 * @(#)AbstractFloatValueFromCharSequenceParser.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Parses a JSON {@code number} from a {@link CharSequence}.
 * <p>
 * This class should have a type parameter for the return value of its parse
 * methods. Unfortunately Java does not support type parameters for primitive
 * types. As a workaround we use {@code long}. A {@code long} has enough bits to
 * fit a {@code double} value or a {@code float} value.
 * <p>
 * See {@link JsonDoubleParser} for the grammar of {@code Number}.
 */
abstract class AbstractJsonFloatingPointBitsFromCharSequence extends AbstractFloatValueParser {

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * Parses a {@code number} production.
     * <p>
     * See {@link JsonDoubleParser} for the grammar of {@code number}.
     *
     * @param str    a string containing a {@code FloatingPointLiteralWithWhiteSpace}
     * @param offset start offset of {@code FloatingPointLiteralWithWhiteSpace} in {@code str}
     * @param length length of {@code FloatingPointLiteralWithWhiteSpace} in {@code str}
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public long parseNumber(CharSequence str, int offset, int length) {
        final int endIndex = offset + length;
        if (offset < 0 || length == 0 || endIndex > str.length()) {
            return PARSE_ERROR;
        }
        int index = offset;
        char ch = str.charAt(index);

        // Parse optional minus sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative) {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            if (ch == 0) {
                return PARSE_ERROR;
            }
        }

        // Parse optional leading zero
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            if (ch == '0') {
                return PARSE_ERROR;
            }
        }

        // Parse significand
        // -----------------
        // Note: a multiplication by a constant is cheaper than an
        //       arbitrary integer multiplication.
        long significand = 0;// significand is treated as an unsigned long
        final int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch1 = 0;
        for (; index < endIndex; index++) {
            ch1 = str.charAt(index);
            if (isDigit(ch1)) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + ch1 - '0';
            } else if (ch1 == '.') {
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                for (; index < endIndex - 8; index += 8) {
                    int eightDigits = tryToParseEightDigits(str, index + 1);
                    if (eightDigits < 0) {
                        break;
                    }
                    // This might overflow, we deal with it later.
                    significand = 100_000_000L * significand + eightDigits;
                }
            } else {
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
        if (ch1 == 'e' || ch1 == 'E') {
            ch1 = ++index < endIndex ? str.charAt(index) : 0;
            boolean neg_exp = ch1 == '-';
            if (neg_exp || ch1 == '+') {
                ch1 = ++index < endIndex ? str.charAt(index) : 0;
            }
            illegal |= !isDigit(ch1);
            do {
                // Guard against overflow
                if (expNumber < AbstractFloatValueParser.MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * expNumber + ch1 - '0';
                }
                ch1 = ++index < endIndex ? str.charAt(index) : 0;
            } while (isDigit(ch1));
            if (neg_exp) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }

        // Check if number is complete
        // ------------------------
        if (illegal || index < endIndex
                || !hasLeadingZero && digitCount == 0) {
            return PARSE_ERROR;
        }

        // Re-parse significand in case of a potential overflow
        // -----------------------------------------------
        final boolean isSignificandTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point
        int exponentOfTruncatedSignificand;
        if (digitCount > 19) {
            significand = 0;
            for (index = significandStartIndex; index < significandEndIndex; index++) {
                ch1 = str.charAt(index);
                if (ch1 == '.') {
                    skipCountInTruncatedDigits++;
                } else {
                    if (Long.compareUnsigned(significand, AbstractFloatValueParser.MINIMAL_NINETEEN_DIGIT_INTEGER) < 0) {
                        significand = 10 * significand + ch1 - '0';
                    } else {
                        break;
                    }
                }
            }
            isSignificandTruncated = (index < significandEndIndex);
            exponentOfTruncatedSignificand = virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return valueOfFloatLiteral(str, offset, endIndex, isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
    }

    private int tryToParseEightDigits(CharSequence str, int offset) {
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

        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    /**
     * Computes a float value from the given components of a {@code number}
     * literal.
     *
     * @param str                            the string that contains the number literal (and maybe more)
     * @param startIndex                     the start index (inclusive) of the number literal
     *                                       inside the string
     * @param endIndex                       the end index (exclusive) of the number literal inside
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
