/*
 * @(#)FastIntegerMath.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FastIntegerMath {
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
     * @param parallel    true if a parallel algorithm may be used
     * @return the computed power of ten
     */
    static BigInteger computePowerOfTen(NavigableMap<Integer, BigInteger> powersOfTen, int n, boolean parallel) {
        if (n < SMALL_POWERS_OF_TEN.length) {
            return SMALL_POWERS_OF_TEN[n];
        }
        if (powersOfTen != null) {
            Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n);
            Integer floorN = floorEntry.getKey();
            if (floorN == n) {
                return floorEntry.getValue();
            } else {
                return parallelMultiply(floorEntry.getValue(), computePowerOfTen(powersOfTen, n - floorN, parallel), parallel);
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
        return floorValue.multiply(diffValue);
    }

    static NavigableMap<Integer, BigInteger> createPowersOfTenFloor16Map() {
        NavigableMap<Integer, BigInteger> powersOfTen;
        powersOfTen = new TreeMap<>();
        powersOfTen.put(0, BigInteger.ONE);
        powersOfTen.put(16, TEN_POW_16);
        return powersOfTen;
    }

    public static long estimateNumBits(long numDecimalDigits) {
        // For the decimal number 10 we need log_2(10) = 3.3219 bits.
        // The following formula uses 3.322 * 1024 = 3401.8 rounded up
        // and adds 1, so that we overestimate but never underestimate
        // the number of bits.
        return (((numDecimalDigits * 3402L) >>> 10) + 1);
    }

    static NavigableMap<Integer, BigInteger> fillPowersOf10Floor16(int from, int to, boolean parallel) {
        // We fill the map with powers of 5
        NavigableMap<Integer, BigInteger> powers = new TreeMap<>();
        powers.put(0, BigInteger.valueOf(5));
        powers.put(16, FIVE_POW_16);
        fillPowersOfNFloor16Recursive(powers, from, to, parallel);

        // Shift map entries to the left to obtain powers of ten
        for (Iterator<Map.Entry<Integer, BigInteger>> iterator = powers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, BigInteger> e = iterator.next();
            e.setValue(e.getValue().shiftLeft(e.getKey()));
        }

        return powers;
    }

    static void fillPowersOfNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int from, int to, boolean parallel) {
        int numDigits = to - from;
        // base case:
        if (numDigits <= 18) {
            return;
        }
        // recursion case:
        int mid = splitFloor16(from, to);
        int n = to - mid;
        if (!powersOfTen.containsKey(n)) {
            fillPowersOfNFloor16Recursive(powersOfTen, from, mid, parallel);
            fillPowersOfNFloor16Recursive(powersOfTen, mid, to, parallel);
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
    static UInt128 fullMultiplication(long x, long y) {//before Java 18
        long x0 = x & 0xffffffffL, x1 = x >>> 32;
        long y0 = y & 0xffffffffL, y1 = y >>> 32;
        long p11 = x1 * y1, p01 = x0 * y1;
        long p10 = x1 * y0, p00 = x0 * y0;

        // 64-bit product + two 32-bit values
        long middle = p10 + (p00 >>> 32) + (p01 & 0xffffffffL);
        return new UInt128(
                // 64-bit product + two 32-bit values
                p11 + (middle >>> 32) + (p01 >>> 32),
                // Add LOW PART and lower half of MIDDLE PART
                (middle << 32) | (p00 & 0xffffffffL));
    }

    /**
     * Returns {@code a * 10}.
     * <p>
     * We compute {@code (a + a * 4) * 2}, which is {@code (a + (a << 2)) << 1}.
     * <p>
     * Expected assembly code on x64:
     * <pre>
     * lea     eax, [rdi+rdi*4]
     * add     eax, eax
     * </pre>
     * Expected assembly code on aarch64:
     * <pre>
     * add     w0, w0, w0, lsl 2
     * lsl     w0, w0, 1
     * </pre>
     */
    public static int mul10(int a) {
        return (a + (a << 2)) << 1;
    }

    /**
     * Returns {@code a * 10}.
     * <p>
     * We compute {@code (a + a * 4) * 2}, which is {@code (a + (a << 2)) << 1}.
     * <p>
     * Expected assembly code on x64:
     * <pre>
     * lea     rax, [rdi+rdi*4]
     * add     rax, rax
     * </pre>
     * Expected assembly code on aarch64:
     * <pre>
     * add     x0, x0, x0, lsl 2
     * lsl     x0, x0, 1
     * </pre>
     */
    public static long mul10L(long a) {
        return (a + (a << 2)) << 1;
    }

    static BigInteger parallelMultiply(BigInteger a, BigInteger b, boolean parallel) {
        return a.multiply(b);
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

    private final static Constructor<BigInteger> bigInteger_IntIntArrayConstructor;

    static {
        Constructor<BigInteger> constructor;
        try {
            constructor = BigInteger.class.getDeclaredConstructor(int.class, int[].class);
            constructor.setAccessible(true);
            constructor.newInstance(0, new byte[1]);
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException |
                 RuntimeException e) {
            constructor = null;
        }
        bigInteger_IntIntArrayConstructor = constructor;
    }

    static BigInteger newBigInteger(int signum, int[] magnitude) {
        try {
            if (bigInteger_IntIntArrayConstructor != null) {
                return bigInteger_IntIntArrayConstructor.newInstance(signum, magnitude);
            }
        } catch (InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = new byte[magnitude.length << 2];
        for (int i = 0; i < magnitude.length; i++) {
            FastDoubleSwar.writeIntBE(bytes, i << 2, magnitude[i]);
        }
        return new BigInteger(signum, bytes);
    }

    private final static Field BigInteger_magField;

    static {
        Field field;
        try {
            field = BigInteger.class.getDeclaredField("mag");
            field.setAccessible(true);
            field.get(BigInteger.ZERO);
        } catch (NoSuchFieldException | IllegalAccessException |
                 RuntimeException e) {
            field = null;
        }
        BigInteger_magField = field;
    }

    static int[] getMagnitude(BigInteger a) {
        try {
            if (BigInteger_magField != null) {
                return (int[]) BigInteger_magField.get(a);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = a.toByteArray();
        int[] ints = new int[(bytes.length + 3) >> 2];
        if (ints.length == 0) {
            return ints;
        }

        int modulo = bytes.length & 3;
        int value = 0;
        for (int i = 0; i < modulo; i++) {
            value = (value << 8) | (bytes[i] & 0xff);
        }
        ints[0] = value;

        int j = modulo == 0 ? 0 : 1;
        for (int i = modulo; i < bytes.length; i += 4) {
            ints[j++] = FastDoubleSwar.readIntBE(bytes, i);
        }
        return ints;
    }
}
