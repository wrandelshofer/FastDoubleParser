/*
 * @(#)ParseDigitsTaskCharArray.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
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


    static BigInteger parseDigits(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        int numDigits = to - from;
        if (numDigits < RECURSION_THRESHOLD) {
            return ParseDigitsTaskCharArray.parseDigitsIterative(str, from, to);
        } else {
            return ParseDigitsTaskCharArray.parseDigitsRecursive(str, from, to, powersOfTen);
        }
    }

    /**
     * Parses digits in exponential time O(e^n).
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
     * Parses digits in exponential time O(e^n).
     */
    static BigInteger parseDigitsRecursive(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
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

        high = FftMultiplier.multiply(high, powersOfTen.get(to - mid));
        return low.add(high);
    }

    /**
     * Parses up to 18 digits in exponential time O(e^n).
     */
    static BigInteger parseDigitsUpTo18(char[] str, int from, int to) {
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
}
