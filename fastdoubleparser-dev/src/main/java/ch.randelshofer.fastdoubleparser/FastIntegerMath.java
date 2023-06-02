/*
 * @(#)FastIntegerMath.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

class FastIntegerMath {
    public static final BigInteger FIVE = BigInteger.valueOf(5);
    final static BigInteger TEN_POW_16 = BigInteger.valueOf(10_000_000_000_000_000L);
    final static BigInteger FIVE_POW_16 = BigInteger.valueOf(152_587_890_625L);
    private final static BigInteger[] SMALL_POWERS_OF_TEN = new BigInteger[]{
            BigInteger.ONE,
            BigInteger.TEN,
            BigInteger.valueOf(100L),
            BigInteger.valueOf(1_000L),
            BigInteger.valueOf(10_000L),
            BigInteger.valueOf(100_000L),
            BigInteger.valueOf(1_000_000L),
            BigInteger.valueOf(10_000_000L),
            BigInteger.valueOf(100_000_000L),
            BigInteger.valueOf(1_000_000_000L),
            BigInteger.valueOf(10_000_000_000L),
            BigInteger.valueOf(100_000_000_000L),
            BigInteger.valueOf(1_000_000_000_000L),
            BigInteger.valueOf(10_000_000_000_000L),
            BigInteger.valueOf(100_000_000_000_000L),
            BigInteger.valueOf(1_000_000_000_000_000L)
    };

    /**
     * Don't let anyone instantiate this class.
     */
    private FastIntegerMath() {

    }

    /**
     * Computes the n-th power of ten.
     *
     * @param powersOfTen A map with pre-computed powers of ten
     * @param n           the power
     * @return the computed power of ten
     */
    static BigInteger computePowerOfTen(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
        if (n < SMALL_POWERS_OF_TEN.length) {
            return SMALL_POWERS_OF_TEN[n];
        }
        if (powersOfTen != null) {
            Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n);
            Integer floorN = floorEntry.getKey();
            if (floorN == n) {
                return floorEntry.getValue();
            } else {
                return FftMultiplier.multiply(floorEntry.getValue(), computePowerOfTen(powersOfTen, n - floorN));
            }
        }
        return FIVE.pow(n).shiftLeft(n);
    }

    /**
     * Computes 10<sup>n&~15</sup>.
     */
    static BigInteger computeTenRaisedByNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
        n = n & ~15;
        Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n);
        int floorPower = floorEntry.getKey();
        BigInteger floorValue = floorEntry.getValue();
        if (floorPower == n) {
            return floorValue;
        }
        int diff = n - floorPower;
        BigInteger diffValue = powersOfTen.get(diff);
        if (diffValue == null) {
            diffValue = computeTenRaisedByNFloor16Recursive(powersOfTen, diff);
            powersOfTen.put(diff, diffValue);
        }
        return FftMultiplier.multiply(floorValue, diffValue);
    }

    static NavigableMap<Integer, BigInteger> createPowersOfTenFloor16Map() {
        NavigableMap<Integer, BigInteger> powersOfTen;
        powersOfTen = new TreeMap<>();
        powersOfTen.put(0, BigInteger.ONE);
        powersOfTen.put(16, TEN_POW_16);
        return powersOfTen;
    }

    /** Gives the number of bits necessary to store given number of decimal digits.
     *  According to tests, overestimation is 1 bit at most for numbers like "999...",
     *  as the smallest one is "100..." additional 4 bits overestimation can occur.
     * @param numDecimalDigits number of decimal digits, expected to be positive
     * @return estimated number of bits
     */
    public static long estimateNumBits(int numDecimalDigits) {
        // For the decimal digit we need log_2(10) = 3.32192809488736234787 bits.
        // The following formula uses 3.32192809488736234787 * 1073741824 (=2^30) = 3566893131.8 rounded up
        // and adds 1, so that we overestimate but never underestimate the number of bits.
        return (((numDecimalDigits * 3_566_893_132L) >>> 30) + 1);
    }

    /**
     * Fills a map with powers of 10 floor 16.
     *
     * @param from the start index of the character sequence that contains the digits
     * @param to   the end index of the character sequence that contains the digits
     * @return the filled map
     */
    static NavigableMap<Integer, BigInteger> fillPowersOf10Floor16(int from, int to) {
        // Fill the map with powers of 5
        NavigableMap<Integer, BigInteger> powers = new TreeMap<>();
        powers.put(0, BigInteger.valueOf(5));
        powers.put(16, FIVE_POW_16);
        fillPowersOfNFloor16Recursive(powers, from, to);

        // Shift map entries to the left to obtain powers of ten
        for (Iterator<Map.Entry<Integer, BigInteger>> iterator = powers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, BigInteger> e = iterator.next();
            e.setValue(e.getValue().shiftLeft(e.getKey()));
        }

        return powers;
    }

    static void fillPowersOfNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int from, int to) {
        int numDigits = to - from;
        // base case:
        if (numDigits <= 18) {
            return;
        }
        // recursion case:
        int mid = splitFloor16(from, to);
        int n = to - mid;
        if (!powersOfTen.containsKey(n)) {
            fillPowersOfNFloor16Recursive(powersOfTen, from, mid);
            fillPowersOfNFloor16Recursive(powersOfTen, mid, to);
            powersOfTen.put(n, computeTenRaisedByNFloor16Recursive(powersOfTen, n));
        }
    }

    /**
     * Computes {@code uint128 product = (uint64)x * (uint64)y}.
     * <p>
     * References:
     * <dl>
     *     <dt>Getting the high part of 64 bit integer multiplication</dt>
     *     <dd><a href="https://stackoverflow.com/questions/28868367/getting-the-high-part-of-64-bit-integer-multiplication">
     *         stackoverflow</a></dd>
     * </dl>
     *
     * @param x uint64 factor x
     * @param y uint64 factor y
     * @return uint128 product of x and y
     */
    static UInt128 fullMultiplication(long x, long y) {//since Java 18
        return new UInt128(Math.unsignedMultiplyHigh(x, y), x * y);
    }

    static int splitFloor16(int from, int to) {
        int mid = (from + to) >>> 1;// split in half
        mid = to - (((to - mid + 15) >> 4) << 4);// make numDigits of low a multiple of 16
        return mid;
    }

    static class UInt128 {
        final long high, low;

        private UInt128(long high, long low) {
            this.high = high;
            this.low = low;
        }
    }
}
