/*
 * @(#)java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Map;

import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.SYNTAX_ERROR;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.splitFloor16;

/**
 * Parses digits in exponential time O(e^n).
 */
class ParseDigitsTaskCharArray {
    /**
     * Don't let anyone instantiate this class.
     */
    private ParseDigitsTaskCharArray() {
    }

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
    static final int RECURSION_THRESHOLD = 400;


    /**
     * Parses digits in quadratic time O(N<sup>2</sup>).
     */
    static BigInteger parseDigitsIterative(char[] str, int from, int to) {
        int numDigits = to - from;

        BigSignificand bigSignificand = new BigSignificand(FastIntegerMath.estimateNumBits(numDigits));
        int preroll = from + (numDigits & 7);
        int value = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = value >= 0;
        bigSignificand.add(value);
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            bigSignificand.fma(100_000_000, addend);
        }
        if (!success) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        return bigSignificand.toBigInteger();
    }

    /**
     * Parses digits in O(N log N (log log N)) time.
     * <p>
     * A conventional recursive algorithm would require O(N<sup>1.5</sup>).
     * We achieve better performance by performing multiplications of long bit sequences
     * in the frequencey domain.
     */
    static BigInteger parseDigitsRecursive(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        int numDigits = to - from;

        // Base case: Short sequences can be parsed iteratively.
        if (numDigits <= RECURSION_THRESHOLD) {
            return parseDigitsIterative(str, from, to);
        }

        // Recursion case: Split large sequences up into two parts. The lower part is a multiple of 16 digits.
        int mid = splitFloor16(from, to);
        BigInteger high = parseDigitsRecursive(str, from, mid, powersOfTen);
        BigInteger low = parseDigitsRecursive(str, mid, to, powersOfTen);

        high = FftMultiplier.multiply(high, powersOfTen.get(to - mid), to - mid);
        return low.add(high);
    }
}
