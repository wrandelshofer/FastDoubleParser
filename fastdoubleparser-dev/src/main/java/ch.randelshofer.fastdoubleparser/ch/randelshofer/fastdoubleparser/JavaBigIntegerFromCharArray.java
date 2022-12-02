/*
 * @(#)JavaBigIntegerFromCharArray.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static ch.randelshofer.fastdoubleparser.FastIntegerMath.fillPowersOfTenFloor16;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.parallelMultiply;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.splitFloor16;

class JavaBigIntegerFromCharArray extends AbstractNumberParser {
    /**
     * Threshold on the number of digits for selecting the
     * recursive algorithm instead of the iterative algorithm.
     * <p>
     * Set this to {@link Integer#MAX_VALUE} if you only want to use the
     * iterative algorithm.
     * <p>
     * Set this to {@code 0} if you only want to use the recursive algorithm.
     * <p>
     * Rationale for choosing a specific threshold value:
     * The iterative algorithm has a smaller constant overhead than the
     * recursive algorithm. We speculate that we break even somewhere at twice
     * the threshold value.
     */
    public static final int RECURSION_THRESHOLD = 128;
    /**
     * Threshold for single-threaded algorithm vs. multi-threaded algorithm.
     * <p>
     * Set this to {@link Integer#MAX_VALUE} if you only want to use
     * the single-threaded algorithm.
     * <p>
     * Set this to {@code 0} if you only want to use the multi-threaded
     * algorithm.
     * <p>
     * Rationale for choosing a specific threshold value:
     * We speculate that we need to perform at least 10,000 CPU cycles
     * before it is worth using multiple threads.
     */
    private final static int DEFAULT_PARALLEL_THRESHOLD = 1024;

    /**
     * Parses digits in exponential time O(e^n).
     */
    static BigInteger parseDigitsIterative(char[] str, int from, int to) {
        int numDigits = to - from;
        BigSignificand bigSignificand = new BigSignificand(FastIntegerMath.estimateNumBits(numDigits));
        int preroll = from + (numDigits & 7);
        bigSignificand.add(FastDoubleSwar.parseUpTo7Digits(str, from, preroll));
        for (from = preroll; from < to; from += 8) {
            bigSignificand.fma(100_000_000, FastDoubleSwar.parseEightDigits(str, from));
        }

        return bigSignificand.toBigInteger();
    }

    /**
     * Parses digits in exponential time O(e^n).
     */
    private static BigInteger parseDigitsRecursive(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        // Base case: All sequences of 18 or fewer digits fit into a long.
        int numDigits = to - from;
        if (numDigits <= 18) {
            return parseDigitsUpTo18(str, from, to);
        }
        if (numDigits <= RECURSION_THRESHOLD) {
            return parseDigitsIterative(str, from, to);
        }

        // Recursion case: Sequences of more than 18 digits do not fit into a long.
        int mid = splitFloor16(from, to);
        BigInteger high = parseDigitsRecursive(str, from, mid, powersOfTen);
        BigInteger low = parseDigitsRecursive(str, mid, to, powersOfTen);

        high = high.multiply(powersOfTen.get(to - mid));
        return low.add(high);
    }

    /**
     * Parses up to 18 digits in exponential time O(e^n).
     */
    private static BigInteger parseDigitsUpTo18(char[] str, int from, int to) {
        int numDigits = to - from;
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0;
        for (from = preroll; from < to; from += 8) {
            int result = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= result >= 0;
            significand = significand * 100_000_000L + result;
        }
        if (!success) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        return BigInteger.valueOf(significand);
    }

    /**
     * Parses a {@code BigIntegerLiteral} as specified in {@link JavaBigIntegerParser}.
     *
     * @return result (always non-null)
     * @throws NumberFormatException if parsing fails
     */
    public BigInteger parseBigIntegerLiteral(char[] str, int offset, int length, boolean parallel)
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

        // Parse '0x' | '0X' character sequence
        // ---------------------------
        final boolean hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            ch = index + 1 < endIndex ? str[index + 1] : 0;
            if (ch == 'x' || ch == 'X') {
                return parseHexBigIntegerLiteral(str, index + 2, endIndex, isNegative);
            }
        }

        return parseDecBigIntegerLiteral(str, index, endIndex, isNegative, parallelThreshold);
    }

    private BigInteger parseDecBigIntegerLiteral(char[] str, int from, int to, boolean isNegative, int parallelThreshold) {
        Map<Integer, BigInteger> powersOfTen = fillPowersOfTenFloor16(from, to, parallelThreshold < Integer.MAX_VALUE);
        int numDigits = to - from;
        BigInteger result;
        if (numDigits < parallelThreshold) {
            result = parseDigitsRecursive(str, from, to, powersOfTen);
        } else {
            result = new ParseDigitsTask(str, from, to, powersOfTen, parallelThreshold).compute();
        }
        return isNegative ? result.negate() : result;
    }

    private BigInteger parseHexBigIntegerLiteral(char[] str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (numDigits == 0) {
            throw new NumberFormatException(SYNTAX_ERROR);
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

    /**
     * Parses digits in exponential time O(e^n).
     */
    static class ParseDigitsTask extends RecursiveTask<BigInteger> {
        private final int from, to;
        private final char[] str;
        private final Map<Integer, BigInteger> powersOfTen;
        private final int parallelThreshold;

        ParseDigitsTask(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen, int parallelThreshold) {
            this.from = from;
            this.to = to;
            this.str = str;
            this.powersOfTen = powersOfTen;
            this.parallelThreshold = parallelThreshold;
        }

        protected BigInteger compute() {
            int range = to - from;
            // Base case:
            if (range <= parallelThreshold) {
                return parseDigitsRecursive(str, from, to, powersOfTen);
            }
            // Recursion case:
            int mid = splitFloor16(from, to);
            ParseDigitsTask high = new ParseDigitsTask(str, from, mid, powersOfTen, parallelThreshold);
            ParseDigitsTask low = new ParseDigitsTask(str, mid, to, powersOfTen, parallelThreshold);
            // perform about half the work locally
            low.fork();
            BigInteger highValue = parallelMultiply(high.compute(), powersOfTen.get(to - mid), true);
            return low.join().add(highValue);
        }
    }
}
