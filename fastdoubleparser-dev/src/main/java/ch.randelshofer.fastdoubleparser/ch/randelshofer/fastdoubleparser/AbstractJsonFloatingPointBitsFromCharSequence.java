/*
 * @(#)AbstractJsonFloatingPointBitsFromCharSequence.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
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
    public final long parseNumber(CharSequence str, int offset, int length) {
        final int endIndex = checkBounds(str.length(), offset, length);
        int index = offset;
        char ch = charAt(str, index, endIndex);

        // Parse optional minus sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative) {
            ch = charAt(str, ++index, endIndex);
            if (ch == 0) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        // Parse optional leading zero
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            ch = charAt(str, ++index, endIndex);
            if (ch == '0') {
                throw new NumberFormatException(SYNTAX_ERROR);
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
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            if (FastDoubleSwar.isDigit(ch)) {
                // This might overflow, we deal with it later.
                significand = 10 * significand + ch - '0';
            } else if (ch == '.') {
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                /*
                for (; index < endIndex - 4; index += 4) {
                    int fourDigits = FastDoubleSwar.tryToParseFourDigits(str, index + 1);
                    if (fourDigits < 0) {
                        break;
                    }
                    // This might overflow, we deal with it later.
                    significand = 10_000L * significand + fourDigits;
                }*/
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
        if ((ch | 0x20) == 'e') {// equals ignore case
            ch = charAt(str, ++index, endIndex);
            boolean isExponentNegative = ch == '-';
            if (isExponentNegative || ch == '+') {
                ch = charAt(str, ++index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch);
            do {
                // Guard against overflow
                if (expNumber < AbstractFloatValueParser.MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * expNumber + ch - '0';
                }
                ch = charAt(str, ++index, endIndex);
            } while (FastDoubleSwar.isDigit(ch));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }

        // Check if number is complete
        // ------------------------
        if (illegal || index < endIndex
                || !hasLeadingZero && digitCount == 0) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }

        // Re-parse significand in case of a potential overflow
        // -----------------------------------------------
        final boolean isSignificandTruncated;
        int skipCountInTruncatedDigits = 0;//counts +1 if we skipped over the decimal point
        int exponentOfTruncatedSignificand;
        if (digitCount > 19) {
            significand = 0;
            for (index = significandStartIndex; index < significandEndIndex; index++) {
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
            isSignificandTruncated = (index < significandEndIndex);
            exponentOfTruncatedSignificand = virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return valueOfFloatLiteral(str, offset, endIndex, isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
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
