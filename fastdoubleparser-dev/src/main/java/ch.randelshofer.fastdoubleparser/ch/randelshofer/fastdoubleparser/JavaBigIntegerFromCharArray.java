/*
 * @(#)JavaBigIntegerFromCharArray.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Map;

import static ch.randelshofer.fastdoubleparser.FastIntegerMath.fillPowersOf10Floor16;
import static ch.randelshofer.fastdoubleparser.ParseDigitsTaskCharArray.DEFAULT_PARALLEL_THRESHOLD;
import static ch.randelshofer.fastdoubleparser.ParseDigitsTaskCharArray.parseDigits;

class JavaBigIntegerFromCharArray extends AbstractNumberParser {
    public final static int MAX_INPUT_LENGTH = 1_292_782_622;

    /**
     * The resulting value must fit into {@code 2^31 - 1} bits.
     * The decimal representation of {@code 2^31 - 1} has 646,456,993 digits.
     */
    private static final int MAX_DECIMAL_DIGITS = 646_456_993;


    /**
     * Parses a {@code BigIntegerLiteral} as specified in {@link JavaBigIntegerParser}.
     *
     * @return result (always non-null)
     * @throws NumberFormatException if parsing fails
     */
    public BigInteger parseBigIntegerLiteral(char[] str, int offset, int length, int radix, boolean parallel)
            throws NumberFormatException {
        int parallelThreshold = parallel ? DEFAULT_PARALLEL_THRESHOLD : Integer.MAX_VALUE;
        final int endIndex = offset + length;
        if (offset < 0 || endIndex < offset || endIndex > str.length || length > MAX_INPUT_LENGTH) {
            throw new IllegalArgumentException(ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
        }
        // Parse optional sign
        // -------------------
        int index = offset;
        char ch = str[index];
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str[index] : 0;
            if (ch == 0) {
                throw new NumberFormatException(SYNTAX_ERROR);
            }
        }

        switch (radix) {
        case 10:
            return parseDecDigits(str, index, endIndex, isNegative, parallelThreshold);
        case 16:
            return parseHexDigits(str, index, endIndex, isNegative);
        default:
            return new BigInteger(new String(str, offset, length), radix);
        }
    }

    private BigInteger parseDecDigits(char[] str, int from, int to, boolean isNegative, int parallelThreshold) {
        int numDigits = to - from;
        if (numDigits > 18) {
            return parseManyDecDigits(str, from, to, isNegative, parallelThreshold);
        }
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0;
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            significand = significand * 100_000_000L + addend;
        }
        if (!success) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        return BigInteger.valueOf(isNegative ? -significand : significand);
    }

    private BigInteger parseHexDigits(char[] str, int from, int to, boolean isNegative) {
        from = skipZeroes(str, from, to);
        int numDigits = to - from;
        if (numDigits <= 0) {
            return BigInteger.ZERO;
        }
        byte[] bytes = new byte[((numDigits + 1) >> 1) + 1];
        int index = 1;
        boolean illegalDigits = false;

        if ((numDigits & 1) != 0) {
            char chLow = str[from++];
            int valueLow = AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            bytes[index++] = (byte) valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from + ((to - from) & 7);
        for (; from < prerollLimit; from += 2) {
            char chHigh = str[from];
            char chLow = str[from + 1];
            int valueHigh = chHigh >= 128 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chHigh];
            int valueLow = chLow >= 128 ? AbstractFloatValueParser.OTHER_CLASS : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            bytes[index++] = (byte) (valueHigh << 4 | valueLow);
            illegalDigits |= valueHigh < 0 || valueLow < 0;
        }
        for (; from < to; from += 8, index += 4) {
            long value = FastDoubleSwar.tryToParseEightHexDigits(str, from);
            FastDoubleSwar.writeIntBE(bytes, index, (int) value);
            illegalDigits |= value < 0;
        }
        if (illegalDigits) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        BigInteger result = new BigInteger(bytes);
        return isNegative ? result.negate() : result;
    }

    private BigInteger parseManyDecDigits(char[] str, int from, int to, boolean isNegative, int parallelThreshold) {
        from = skipZeroes(str, from, to);
        Map<Integer, BigInteger> powersOfTen = fillPowersOf10Floor16(from, to, parallelThreshold < Integer.MAX_VALUE);
        int numDigits = to - from;
        if (numDigits > MAX_DECIMAL_DIGITS) {
            throw new NumberFormatException(VALUE_EXCEEDS_LIMITS);
        }
        BigInteger result = parseDigits(str, from, to, powersOfTen, parallelThreshold);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(char[] str, int from, int to) {
        while (from < to - 8 && FastDoubleSwar.isEightZeroes(str, from)) {
            from += 8;
        }
        while (from < to && str[from] == '0') {
            from++;
        }
        return from;
    }

}
