/*
 * @(#)ParseDigitsTaskByteArray.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.SYNTAX_ERROR;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.parallelMultiply;
import static ch.randelshofer.fastdoubleparser.FastIntegerMath.splitFloor16;

/**
 * Parses digits in exponential time O(e^n).
 */
class ParseDigitsTaskByteArray extends RecursiveTask<BigInteger> {
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
    public static final int RECURSION_THRESHOLD = 400;
    /**
     * Threshold on the number of digits for selecting the multi-threaded
     * algorithm instead of the single-thread algorithm.
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
    static final int DEFAULT_PARALLEL_THRESHOLD = 1024;
    private final int from, to;
    private final byte[] str;
    private final Map<Integer, BigInteger> powersOfTen;
    private final int parallelThreshold;

    ParseDigitsTaskByteArray(byte[] str, int from, int to, Map<Integer, BigInteger> powersOfTen, int parallelThreshold) {
        this.from = from;
        this.to = to;
        this.str = str;
        this.powersOfTen = powersOfTen;
        this.parallelThreshold = parallelThreshold;
    }

    static BigInteger parseDigits(byte[] str, int from, int to, Map<Integer, BigInteger> powersOfTen, int parallelThreshold) {
        int numDigits = to - from;
        if (numDigits < RECURSION_THRESHOLD) {
            return parseDigitsIterative(str, from, to);
        } else if (numDigits < parallelThreshold) {
            return ParseDigitsTaskByteArray.parseDigitsRecursive(str, from, to, powersOfTen);
        } else {
            return new ParseDigitsTaskByteArray(str, from, to, powersOfTen, parallelThreshold).compute();
        }
    }

    /**
     * Parses digits in exponential time O(e^n).
     */
    static BigInteger parseDigitsIterative(byte[] str, int from, int to) {
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
    static BigInteger parseDigitsRecursive(byte[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
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

        //high = high.multiply(powersOfTen.get(to - mid));
        high = SchoenhageStrassenMultiplier_tbuktu.multiply(high, powersOfTen.get(to - mid), false);
        return low.add(high);
    }

    /**
     * Parses up to 18 digits in exponential time O(e^n).
     */
    private static BigInteger parseDigitsUpTo18(byte[] str, int from, int to) {
        int numDigits = to - from;
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0;
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigitsUtf8(str, from);
            success &= addend >= 0;
            significand = significand * 100_000_000L + addend;
        }
        if (!success) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        return BigInteger.valueOf(significand);
    }

    protected BigInteger compute() {
        int range = to - from;
        // Base case:
        if (range <= parallelThreshold) {
            return parseDigitsRecursive(str, from, to, powersOfTen);
        }
        // Recursion case:
        int mid = splitFloor16(from, to);
        ParseDigitsTaskByteArray high = new ParseDigitsTaskByteArray(str, from, mid, powersOfTen, parallelThreshold);
        ParseDigitsTaskByteArray low = new ParseDigitsTaskByteArray(str, mid, to, powersOfTen, parallelThreshold);
        // perform about half the work locally
        low.fork();
        BigInteger highValue = parallelMultiply(high.compute(), powersOfTen.get(to - mid), true);
        //highValue = SchoenhageStrassenMultiplier.multiply(high.compute(), pow, true);
        return low.join().add(highValue);
    }
}
