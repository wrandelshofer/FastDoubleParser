/*
 * @(#)FastDoubleParser.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.util.concurrent.RecursiveTask;


/**
 * Parses a {@code double} from a {@code byte} array.
 */
final class JavaBigDecimalFromByteArray {
    /**
     * See {@link JavaBigDecimalParser}.
     */
    private final static int MAX_DIGIT_COUNT = 536_870_919;
    private final static long MAX_EXPONENT_NUMBER = Integer.MAX_VALUE;

    /**
     * Threshold for recursive algorithm vs. sequential algorithm.
     * <p>
     * Set this to {@link Integer#MAX_VALUE} if you only want to use the
     * sequential algorithm.
     * <p>
     * Set this to {@link 0} if you only want to use the recursive algorithm.
     * <p>
     * Rationale for choosing a specific threshold value:
     * The sequential algorithm is quadratic, the recursive algorithm is
     * linear. We speculate that we break even somewhere at twice the
     * threshold.
     */
    public static final int RECURSIVE_THRESHOLD = 128;

    /**
     * Threshold for single-threaded algorithm vs. multi-threaded algorithm.
     * <p>
     * Set this to {@link Integer#MAX_VALUE} if you only want to use
     * the single-threaded algorithm.
     * <p>
     * Set this to {@link 0} if you only want to use the multi-threaded
     * algorithm.
     * <p>
     * Rationale for choosing a specific threshold value:
     * We speculate that we need to perform at least 10,000 CPU cycles
     * before it is worth using multiple threads.
     */
    public static final int PARALLEL_THRESHOLD = 1024;

    /**
     * Threshold for algorithm for short input sequences vs. algorithm for
     * long input sequences.
     * <p>
     * Set this to {@link Integer#MAX_VALUE} if you only want to use
     * the algorithm for short inputs.
     * <p>
     * Set this to {@code 0} if you only want to use the algorithm for
     * long inputs.
     * <p>
     * Rationale for choosing a specific threshold value:
     * We speculate that we only need to use the algorithm for long inputs
     * if there is no chance, that we can parse the input with the algorithm
     * for short inputs.
     * <pre>
     * optional significant sign = 1
     * 18 significant digits = 18
     * optional decimal point in significant = 1
     * optional exponent = 1
     * optional exponent sign = 1
     * 10 exponent digits = 10
     * </pre>
     */
    public static final int LARGE_THRESHOLD = 1 + 18 + 1 + 1 + 1 + 10;


    /**
     * Creates a new instance.
     */
    public JavaBigDecimalFromByteArray() {

    }

    /**
     * Parses digits using a recursive algorithm in O(n) with a large
     * constant overhead if there are less than about 100 digits.
     *
     * @param str      the input string
     * @param from     the start index of the digit sequence in str (inclusive)
     * @param to       the end index of the digit sequence in str (exclusive)
     * @param exponent the exponent of the digits
     * @return a {@link BigDecimal}
     */
    static BigDecimal parseDigitsRecursive(byte[] str, int from, int to, int exponent) {
        // Base case: All sequences of 18 or fewer digits fit into a long.
        int numDigits = to - from;
        if (numDigits <= 18) {
            return parseDigitsUpTo18(str, from, to, exponent);
        }
        if (numDigits <= RECURSIVE_THRESHOLD) {
            return parseDigitsLinear(str, from, to, exponent);
        }

        // Recursion case: Sequences of more than 18 digits do not fit into a long.
        int mid = (from + to) >>> 1;
        BigDecimal high = parseDigitsRecursive(str, from, mid, exponent + to - mid);
        BigDecimal low = parseDigitsRecursive(str, mid, to, exponent);
        return low.add(high);
    }

    /**
     * Parses up to 18 digits using a linear algorithm in O(n^2), with
     * a small constant overhead.
     *
     * @param str      the input string
     * @param from     the start index of the digit sequence in str (inclusive)
     * @param to       the end index of the digit sequence in str (exclusive)
     * @param exponent the exponent of the digits
     * @return a {@link BigDecimal}
     */
    static BigDecimal parseDigitsUpTo18(byte[] str, int from, int to, int exponent) {
        int numDigits = to - from;
        int significand = 0;
        int prerollLimit = from + (numDigits & 7);
        for (; from < prerollLimit; from++) {
            significand = 10 * (significand) + str[from] - '0';
        }
        long significandL = significand;
        for (; from < to; from += 8) {
            significandL = significandL * 100_000_000L + FastDoubleSwar.parseEightDigitsUtf8(str, from);
        }
        return BigDecimal.valueOf(significandL, -exponent);
    }

    /**
     * Parses digits using a linear algorithm in O(n^2) with a large
     * constant overhead if there are less than 19 digits.
     *
     * @param str      the input string
     * @param from     the start index of the digit sequence in str (inclusive)
     * @param to       the end index of the digit sequence in str (exclusive)
     * @param exponent the exponent of the digits
     * @return a {@link BigDecimal}
     */
    static BigDecimal parseDigitsLinear(byte[] str, int from, int to, int exponent) {
        int numDigits = to - from;
        BigSignificand bigSignificand = new BigSignificand(BigSignificand.estimateNumBits(numDigits));
        int significand = 0;
        int prerollLimit = from + (numDigits & 7);
        for (; from < prerollLimit; from++) {
            significand = 10 * (significand) + str[from] - '0';
        }
        bigSignificand.add(significand);
        for (; from < to; from += 8) {
            bigSignificand.fma(100_000_000, FastDoubleSwar.parseEightDigitsUtf8(str, from));
        }

        return new BigDecimal(bigSignificand.toBigInteger(), -exponent);
    }


    private boolean isDigit(byte c) {
        return (byte) '0' <= c && c <= (byte) '9';
    }

    private BigDecimal parseLargeDecFloatLiteral(byte[] str, int integerPartIndex, int pointIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        boolean hasFractionalPart = exponentIndicatorIndex - pointIndex > 1;
        BigDecimal significand;
        if (hasFractionalPart) {
            significand = parseDigits(str, integerPartIndex, pointIndex, exponent + exponentIndicatorIndex - pointIndex - 1)
                    .add(parseDigits(str, pointIndex + 1, exponentIndicatorIndex, exponent));
        } else {
            significand = parseDigits(str, integerPartIndex, pointIndex, exponent + exponentIndicatorIndex - pointIndex);
        }
        return isNegative ? significand.negate() : significand;
    }

    private BigDecimal parseDigits(byte[] str, int from, int to, int exponent) {
        int numDigits = to - from;
        if (numDigits < PARALLEL_THRESHOLD) {
            return parseDigitsRecursive(str, from, to, exponent);
        }
        return new ParseDigitsTask(from, to, str, exponent).compute();
    }

    public BigDecimal parseFloatingPointLiteral(byte[] str, int offset, int length) {
        if (length >= LARGE_THRESHOLD) {
            return parseLargeFloatingPointLiteral(str, offset, length);
        }
        long significand = 0L;
        final int integerPartIndex;
        int decimalPointIndex = -1;
        final int exponentIndicatorIndex;

        final int endIndex = offset + length;
        int index = offset;
        byte ch = index < endIndex ? str[index] : 0;
        boolean illegal = false;


        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str[index] : 0;
            if (ch == 0) {
                return null;
            }
        }

        // Parse significand
        integerPartIndex = index;
        for (; index < endIndex; index++) {
            ch = str[index];
            if (isDigit(ch)) {
                // This might overflow, we deal with it later.
                significand = 10 * (significand) + ch - '0';
            } else if (ch == '.') {
                illegal |= decimalPointIndex >= 0;
                decimalPointIndex = index;
                for (; index < endIndex - 4; index += 4) {
                    int digits = FastDoubleSwar.tryToParseFourDigitsUtf8(str, index + 1);
                    if (digits < 0) {
                        break;
                    }
                    // This might overflow, we deal with it later.
                    significand = 10_000L * significand + digits;
                }
            } else {
                break;
            }
        }

        final int digitCount;
        final int significandEndIndex = index;
        long exponent;
        if (decimalPointIndex < 0) {
            digitCount = significandEndIndex - integerPartIndex;
            decimalPointIndex = significandEndIndex;
            exponent = 0;
        } else {
            digitCount = significandEndIndex - integerPartIndex - 1;
            exponent = decimalPointIndex - significandEndIndex + 1;
        }

        // Parse exponent number
        // ---------------------
        long expNumber = 0;
        if (ch == 'e' || ch == 'E') {
            exponentIndicatorIndex = index;
            ch = ++index < endIndex ? str[index] : 0;
            boolean isExponentNegative = ch == '-';
            if (isExponentNegative || ch == '+') {
                ch = ++index < endIndex ? str[index] : 0;
            }
            illegal |= !isDigit(ch);
            do {
                // Guard against overflow
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * (expNumber) + ch - '0';
                }
                ch = ++index < endIndex ? str[index] : 0;
            } while (isDigit(ch));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        } else {
            exponentIndicatorIndex = endIndex;
        }
        if (illegal || index < endIndex
                || digitCount == 0
                || exponent < Integer.MIN_VALUE
                || exponent > Integer.MAX_VALUE
                || digitCount > MAX_DIGIT_COUNT) {
            return null;
        }

        if (digitCount <= 18) {
            return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int) exponent);
        }
        return parseDecFloatLiteral(str, integerPartIndex, decimalPointIndex, exponentIndicatorIndex, isNegative, (int) exponent);
    }

    private BigDecimal parseDecFloatLiteral(byte[] str, int integerPartIndex, int pointIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        boolean hasFractionalPart = exponentIndicatorIndex - pointIndex > 1;
        BigDecimal significand;
        if (hasFractionalPart) {
            significand = parseDigits(str, integerPartIndex, pointIndex, exponent + exponentIndicatorIndex - pointIndex - 1)
                    .add(parseDigits(str, pointIndex + 1, exponentIndicatorIndex, exponent));
        } else {
            significand = parseDigits(str, integerPartIndex, pointIndex, exponent + exponentIndicatorIndex - pointIndex);
        }
        return isNegative ? significand.negate() : significand;
    }

    private BigDecimal parseLargeFloatingPointLiteral(byte[] str, int offset, int length) {
        final int integerPartIndex;
        int decimalPointIndex = -1;
        final int exponentIndicatorIndex;

        final int endIndex = offset + length;
        int index = offset;
        byte ch = index < endIndex ? str[index] : 0;
        boolean illegal = false;


        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str[index] : 0;
            if (ch == 0) {
                return null;
            }
        }

        // Parse significand
        // -----------------
        // Count digits of integer part
        integerPartIndex = index;
        while (index < endIndex - 8 && FastDoubleSwar.isEightDigitsUtf8(str, index)) {
            index += 8;
        }
        while (index < endIndex && isDigit(ch = str[index])) {
            index++;
        }
        if (ch == '.') {
            decimalPointIndex = index++;
            // Count digits of fraction part
            while (index < endIndex - 8 && FastDoubleSwar.isEightDigitsUtf8(str, index)) {
                index += 8;
            }
            while (index < endIndex && isDigit(ch = str[index])) {
                index++;
            }
        }

        final int digitCount;
        final int significandEndIndex = index;
        long exponent;
        if (decimalPointIndex < 0) {
            digitCount = significandEndIndex - integerPartIndex;
            decimalPointIndex = significandEndIndex;
            exponent = 0;
        } else {
            digitCount = significandEndIndex - integerPartIndex - 1;
            exponent = decimalPointIndex - significandEndIndex + 1;
        }

        // Parse exponent number
        // ---------------------
        long expNumber = 0;
        if (ch == 'e' || ch == 'E') {
            exponentIndicatorIndex = index;
            ch = ++index < endIndex ? str[index] : 0;
            boolean isExponentNegative = ch == '-';
            if (isExponentNegative || ch == '+') {
                ch = ++index < endIndex ? str[index] : 0;
            }
            illegal = !isDigit(ch);
            do {
                // Guard against overflow
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * (expNumber) + ch - '0';
                }
                ch = ++index < endIndex ? str[index] : 0;
            } while (isDigit(ch));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        } else {
            exponentIndicatorIndex = endIndex;
        }
        if (illegal || index < endIndex
                || digitCount == 0
                || exponent < Integer.MIN_VALUE
                || exponent > Integer.MAX_VALUE
                || digitCount > MAX_DIGIT_COUNT) {
            return null;
        }
        return parseLargeDecFloatLiteral(str, integerPartIndex, decimalPointIndex, exponentIndicatorIndex, isNegative, (int) exponent);
    }

    /**
     * Parses digits using a multi-threaded recursive algorithm in O(n)
     * with a large constant overhead if there are less than about 1000
     * digits.
     */
    static class ParseDigitsTask extends RecursiveTask<BigDecimal> {
        private final int from, to;
        private final byte[] str;
        private final int exponent;

        ParseDigitsTask(int from, int to, byte[] str, int exponent) {
            this.from = from;
            this.to = to;
            this.str = str;
            this.exponent = exponent;
        }

        protected BigDecimal compute() {
            int range = to - from;

            // Base case:
            if (range <= PARALLEL_THRESHOLD) {
                return parseDigitsRecursive(str, from, to, exponent);
            }

            // Recursion case:
            int mid = (from + to) >>> 1;// split in half
            ParseDigitsTask high = new ParseDigitsTask(from, mid, str, exponent + to - mid);
            ParseDigitsTask low = new ParseDigitsTask(mid, to, str, exponent);
            // perform about half the work locally
            high.fork();
            BigDecimal lowValue = low.compute();
            return lowValue.add(high.join());
        }
    }

}