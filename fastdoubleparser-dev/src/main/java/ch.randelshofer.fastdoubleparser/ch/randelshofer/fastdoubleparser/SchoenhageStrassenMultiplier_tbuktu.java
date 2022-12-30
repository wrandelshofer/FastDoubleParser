/*
 * @(#)SchoenhageStrassenMultiplier_tbuktu.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * Provides methods for multiplying two {@link BigInteger}s using the
 * {@code Schönhage-Strassen algorithm}.
 * <p>
 * This code is based on {@code bigint} by Timothy Buktu.
 * <p>
 * References:
 * <dl>
 *     <dt>Schönhage-Strassen algorithm
 *     </dt>
 *     <dd><a href="http://en.wikipedia.org/wiki/Sch%C3%B6nhage%E2%80%93Strassen_algorithm">
 *  wikipedia</a>.</dd>
 *
 *     <dt>bigint, Copyright 2013 © Timothy Buktu, 2-Clause BSD License.
 *     </dt>
 *     <dd><a href="https://github.com/tbuktu/bigint">github.com</a></dd>
 * </dl>
 */
class SchoenhageStrassenMultiplier_tbuktu {
    private static final int SCHOENHAGE_STRASSEN_THRESHOLD = 8700;

    /**
     * Returns a BigInteger whose value is {@code (a * b)}.
     *
     * @param a value a
     * @param b value b
     * @return {@code a * b}
     * @implNote An implementation may offer better algorithmic
     * performance when {@code a == b}.
     */
    public static BigInteger multiply(BigInteger a, BigInteger b, boolean parallel) {
        if (a.signum() == 0 || b.signum() == 0) {
            return BigInteger.ZERO;
        }

        int alen = a.bitLength();
        int blen = b.bitLength();

        if ((alen < SCHOENHAGE_STRASSEN_THRESHOLD) || (blen < SCHOENHAGE_STRASSEN_THRESHOLD)) {
            return parallel ? a.parallelMultiply(b) : a.multiply(b);
        } else {
            return multiplySchoenhageStrassen(a, b, parallel);
        }
    }

    static int[] multiply(int[] a, int[] b, boolean parallel) {
        int alen = a.length * 8;
        int blen = b.length * 8;
        if ((alen < SCHOENHAGE_STRASSEN_THRESHOLD) || (blen < SCHOENHAGE_STRASSEN_THRESHOLD)) {
            BigInteger aBigInt = FastIntegerMath.newBigInteger(1, a);
            BigInteger bBigInt = FastIntegerMath.newBigInteger(1, b);
            return FastIntegerMath.getMagnitude(SchoenhageStrassenMultiplier_tbuktu.multiply(aBigInt, bBigInt, parallel));
        } else {
            return multiplySchoenhageStrassen(a, b, parallel);
        }
    }

    /**
     * Multiplies two {@link BigInteger}s using the
     * <a href="http://en.wikipedia.org/wiki/Sch%C3%B6nhage%E2%80%93Strassen_algorithm">
     * Schoenhage-Strassen algorithm</a> algorithm.
     *
     * @param a        the first factor
     * @param b        the second factor
     * @param parallel whether to run in parallel
     * @return a*b
     */
    static BigInteger multiplySchoenhageStrassen(BigInteger a, BigInteger b, boolean parallel) {
        // remove any minus signs, multiply, then fix sign
        int signum = a.signum() * b.signum();
        if (a.signum() < 0) {
            a = a.negate();
        }
        if (b.signum() < 0) {
            b = b.negate();
        }

        int[] cArr = multiplySchoenhageStrassen(FastIntegerMath.getMagnitude(a), FastIntegerMath.getMagnitude(b), parallel);
        return FastIntegerMath.newBigInteger(signum, cArr);
    }

    private static Field BigInteger_magField;

    /**
     * Returns a BigInteger whose value is {@code (this<sup>2</sup>)},
     * using multiple threads if the numbers are sufficiently large.
     *
     * @return {@code this<sup>2</sup>}
     */
    static BigInteger square(BigInteger b, boolean parallel) {
        return multiplySchoenhageStrassen(b, b, parallel);
    }

    /**
     * This is the core Schoenhage-Strassen method. It multiplies two <b>positive</b> numbers given as
     * int arrays, i.e. in base 2<sup>32</sup>.
     * Positive means an int is always interpreted as an unsigned number, regardless of the sign bit.<br/>
     * The arrays must be ordered most significant to least significant, so the most significant digit
     * must be at index 0.<br/>
     * If <code>a==b</code>, the DFT for b is omitted which saves roughly 1/4 of the execution time.
     * <p/>
     * The Schoenhage-Strassen algorithm works as follows:
     * <ol>
     *   <li>Given numbers a and b, split both numbers into pieces of length 2<sup>n-1</sup> bits.
     *       See the code for how n is calculated.</li>
     *   <li>Take the low n+2 bits of each piece of a, zero-pad them to 3n+5 bits,
     *       and concatenate them to a new number u.</li>
     *   <li>Do the same for b to obtain v.</li>
     *   <li>Calculate all pieces of gamma by multiplying u and v (using Schoenhage-Strassen or another
     *       algorithm).</li>
     *   <li>Split gamma into pieces of 3n+5 bits.</li>
     *   <li>Calculate z'<sub>i</sub> = gamma<sub>i</sub> + gamma<sub>i+2*2<sup>n</sup></sub> -
     *       gamma<sub>i+2<sup>n</sup></sub> - gamma<sub>i+3*2<sup>n</sup></sub> and reduce modulo
     *       2<sup>n+2</sup>.<br/>
     *       z'<sub>i</sub> will be the i-th piece of a*b mod 2<sup>n+2</sup>.</li>
     *   <li>Pad the pieces of a and b from step 1 to 2<sup>n</sup>+1 bits.</li>
     *   <li>Perform a
     *       <a href="http://en.wikipedia.org/wiki/Discrete_Fourier_transform_%28general%29#Number-theoretic_transform">
     *       Discrete Fourier Transform</a> (DFT) on the padded pieces.</li>
     *   <li>Calculate all pieces of z" by multiplying the i-th piece of a by the i-th piece of b.</li>
     *   <li>Perform an Inverse Discrete Fourier Transform (IDFT) on z". z" will contain all pieces of
     *       a*b mod F<sub>n</sub> where F<sub>n</sub>=2<sup>2<sup>n</sup></sup>+1.</li>
     *   <li>Calculate all pieces of z such that each piece is congruent to z' modulo 2<sup>n+2</sup> and congruent to
     *       z" modulo F<sub>n</sub>. This is done using the
     *       <a href="http://en.wikipedia.org/wiki/Chinese_remainder_theorem">Chinese remainder theorem</a>.</li>
     *   <li>Calculate c by adding z<sub>i</sub> * 2<sup>i*2<sup>n-1</sup></sup> for all i, where z<sub>i</sub> is the
     *       i-th piece of z.</li>
     *   <li>Return c reduced modulo 2<sup>2<sup>m</sup></sup>+1. See the code for how m is calculated.</li>
     * </ol>
     * <p>
     * References:
     * <ol>
     *   <li><a href="http://en.wikipedia.org/wiki/Sch%C3%B6nhage%E2%80%93Strassen_algorithm">
     *       Wikipedia article</a>
     *   <li><a href="http://www.scribd.com/doc/68857222/Schnelle-Multiplikation-gro%C3%9Fer-Zahlen">
     *       Arnold Schoenhage und Volker Strassen: Schnelle Multiplikation grosser Zahlen, Computing 7, 1971,
     *       Springer-Verlag, S. 281-292</a></li>
     *   <li><a href="http://malte-leip.net/beschreibung_ssa.pdf">Eine verstaendliche Beschreibung des
     *       Schoenhage-Strassen-Algorithmus</a></li>
     *   <li><a href="http://www.loria.fr/~gaudry/publis/issac07.pdf">A GMP-based Implementation of
     *       Schoenhage-Strassen's Large Integer Multiplication Algorithm</a></li>
     * </ol>
     *
     * @param a        value a
     * @param b        value b
     * @param parallel whether to run in parallel
     * @return a*b
     */
    static int[] multiplySchoenhageStrassen(int[] a, int[] b, boolean parallel) {
        boolean square = a == b;

        // set M to the number of binary digits in a or b, whichever is greater
        int M = Math.max(a.length * 32, b.length * 32);

        // find the lowest m such that m>=log2(2M)
        int m = 32 - Integer.numberOfLeadingZeros(2 * M - 1 - 1);

        int n = m / 2 + 1;

        assert n >= 6 : "need n>=6 for SchoenhageStrasse, n=" + n;

        // split a and b into pieces 1<<(n-1) bits long; assume n>=6 so pieces start and end at int boundaries
        boolean even = m % 2 == 0;
        int numPieces = even ? 1 << n : 1 << (n + 1);
        int pieceSize = 1 << (n - 1 - 5);   // in ints

        // zi mod 2^(n+2): build u and v from a and b, allocating 3n+5 bits in u and v per n+2 bits from a and b, resp.
        int numPiecesA = (a.length + pieceSize) / pieceSize;
        int[] u = new int[(numPiecesA * (3 * n + 5) + 31) / 32];
        int uBitLength = 0;
        for (int i = 0; i < numPiecesA && i * pieceSize < a.length; i++) {
            appendBits(u, uBitLength, a, i * pieceSize, n + 2);
            uBitLength += 3 * n + 5;
        }
        int[] gamma;
        if (square) {
            /*
            try {
                if (BigInteger_magField ==null) {
                    Field field = BigInteger.class.getDeclaredField("mag");
                    field.setAccessible(true);
                    BigInteger_magField =field;
                }
                int[] ints = (int[]) BigInteger_magField.get(a);
                if (!Arrays.equals(q,ints)) {
                    System.out.println("oops");
                }
                return ints;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }*/
            gamma = FastIntegerMath.getMagnitude(square(FastIntegerMath.newBigInteger(1, u), parallel));   // gamma = u * u
        } else {
            int numPiecesB = (b.length + pieceSize) / pieceSize;
            int[] v = new int[(numPiecesB * (3 * n + 5) + 31) / 32];
            int vBitLength = 0;
            for (int i = 0; i < numPiecesB && i * pieceSize < b.length; i++) {
                appendBits(v, vBitLength, b, i * pieceSize, n + 2);
                vBitLength += 3 * n + 5;
            }
            /*
            try {
                if (BigInteger_magField ==null) {
                    Field field = BigInteger.class.getDeclaredField("mag");
                    field.setAccessible(true);
                    BigInteger_magField =field;
                }
                int[] ints = (int[]) BigInteger_magField.get(a);
                if (!Arrays.equals(q,ints)) {
                    System.out.println("oops");
                }
                return ints;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }*/
            gamma = FastIntegerMath.getMagnitude(multiply(FastIntegerMath.newBigInteger(1, u), FastIntegerMath.newBigInteger(1, v), parallel));   // gamma = u * v
        }
        int[][] gammai = splitBits(gamma, 3 * n + 5);
        int halfNumPcs = numPieces / 2;

        int[][] zi = new int[gammai.length][];
        System.arraycopy(gammai, 0, zi, 0, gammai.length);
        for (int i = 0; i < gammai.length - halfNumPcs; i++)
            subModPow2(zi[i], gammai[i + halfNumPcs], n + 2);
        for (int i = 0; i < gammai.length - 2 * halfNumPcs; i++)
            addModPow2(zi[i], gammai[i + 2 * halfNumPcs], n + 2);
        for (int i = 0; i < gammai.length - 3 * halfNumPcs; i++)
            subModPow2(zi[i], gammai[i + 3 * halfNumPcs], n + 2);

        // zr mod Fn
        int targetPieceSize = (1 << (n - 6)) + 1; // assume n>=6
        MutableModFn_tbuktu[] ai = split(a, halfNumPcs, pieceSize, targetPieceSize);
        MutableModFn_tbuktu[] bi = null;
        if (!square) {
            bi = split(b, halfNumPcs, pieceSize, targetPieceSize);
        }
        int omega = even ? 4 : 2;
        if (square) {
            dft(ai, omega, parallel);
            squareElements(ai, parallel);
        } else {
            dft(ai, omega, parallel);
            dft(bi, omega, parallel);
            multiplyElements(ai, bi, parallel);
        }
        MutableModFn_tbuktu[] c = ai;
        idft(c, omega, parallel);
        int[][] cInt = toIntArray(c);

        int[] z = new int[(1 << (m - 5)) + 1];
        // calculate zr mod Fm from zr mod Fn and zr mod 2^(n+2), then add to z
        // note: z is an int[] rather than a MutableBigInteger because MBI.addShifted() seems to be much slower than BI.addShifted()
        for (int i = 0; i < halfNumPcs; i++) {
            int[] eta = i >= zi.length ? new int[(n + 2 + 31) / 32] : zi[i];

            // zi = delta = (zi-c[i]) % 2^(n+2)
            subModPow2(eta, cInt[i], n + 2);

            // z += zr<<shift = [ci + delta*(2^2^n+1)] << [i*2^(n-1)]
            int shift = i * (1 << (n - 1 - 5));   // assume n>=6
            addShifted(z, cInt[i], shift);
            addShifted(z, eta, shift);
            addShifted(z, eta, shift + (1 << (n - 5)));
        }

        MutableModFn_tbuktu.reduce(z);   // assume m>=5
        return z;
    }

    private static int[][] toIntArray(MutableModFn_tbuktu[] a) {
        int[][] aInt = new int[a.length][];
        for (int i = 0; i < a.length; i++)
            aInt[i] = MutableModFn_tbuktu.toIntArrayOdd(a[i].digits);
        return aInt;
    }

    /**
     * Adds two numbers, <code>a</code> and <code>b</code>, after shifting <code>b</code> by
     * <code>numElements</code> elements.<br/>
     * Both numbers are given as <code>int</code> arrays and must be <b>positive</b> numbers
     * (meaning they are interpreted as unsigned).<br/>
     * The result is returned in the first argument.
     * If any elements of b are shifted outside the valid range for <code>a</code>, they are dropped.
     *
     * @param a           a number in base 2<sup>32</sup> starting with the highest digit
     * @param b           a number in base 2<sup>32</sup> starting with the highest digit
     * @param numElements the number of elements
     */
    private static void addShifted(int[] a, int[] b, int numElements) {
        int carry = 0;
        int aIdx = a.length - 1 - numElements;
        int bIdx = b.length - 1;
        int i = Math.min(aIdx, bIdx);
        while (i >= 0) {
            int ai = a[aIdx];
            int sum = ai + b[bIdx] + carry;
            // if signBit(sum) < signBit(a)+signBit(b) then carry = 1; else carry=0
            carry = ((sum >>> 31) - (ai >>> 31) - (b[bIdx] >>> 31)) >>> 31;
            a[aIdx] = sum;
            i--;
            aIdx--;
            bIdx--;
        }
        boolean carryFlag = carry != 0;
        while (carryFlag && aIdx >= 0) {
            a[aIdx]++;
            carryFlag = a[aIdx] == 0;
            aIdx--;
        }
    }

    static Constructor<BigInteger> bigIntegerIntArrayConstructor;


    private static BigInteger newBigInteger(int[] val) {
        try {
            if (bigIntegerIntArrayConstructor == null) {
                Constructor<BigInteger> constructor = BigInteger.class.getDeclaredConstructor(int[].class);
                constructor.setAccessible(true);
                bigIntegerIntArrayConstructor = constructor;
            }
            return bigIntegerIntArrayConstructor.newInstance(val);
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        /*
        if (val.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }
        int[] mag;
        int signum;
        if (val[0] < 0) {
            mag = makePositive(val);
            signum = -1;
        } else {
            mag = trustedStripLeadingZeroInts(val);
            signum = (mag.length == 0 ? 0 : 1);
        }
        /*
        if (mag.length >= MAX_MAG_LENGTH) {
            checkRange();
        }* /
        return newBigInteger(signum, mag);
         */
    }

    /**
     * Returns the input array stripped of any leading zero bytes.
     * Since the source is trusted the copying may be skipped.
     */
    private static int[] trustedStripLeadingZeroInts(int val[]) {
        int vlen = val.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; keep < vlen && val[keep] == 0; keep++)
            ;
        return keep == 0 ? val : java.util.Arrays.copyOfRange(val, keep, vlen);
    }

    /**
     * Takes an array a representing a negative 2's-complement number and
     * returns the minimal (no leading zero ints) unsigned whose value is -a.
     */
    private static int[] makePositive(int a[]) {
        int keep, j;

        // Find first non-sign (0xffffffff) int of input
        for (keep = 0; keep < a.length && a[keep] == -1; keep++)
            ;

        /* Allocate output array.  If all non-sign ints are 0x00, we must
         * allocate space for one extra output int. */
        for (j = keep; j < a.length && a[j] == 0; j++)
            ;
        int extraInt = (j == a.length ? 1 : 0);
        int result[] = new int[a.length - keep + extraInt];

        /* Copy one's complement of input into output, leaving extra
         * int (if it exists) == 0x00 */
        for (int i = keep; i < a.length; i++)
            result[i - keep + extraInt] = ~a[i];

        // Add one to one's complement to generate two's complement
        for (int i = result.length - 1; ++result[i] == 0; i--)
            ;

        return result;
    }

    /**
     * Reads <code>bBitLength</code> bits from <code>b</code>, starting at array index
     * <code>bStart</code>, and copies them into <code>a</code>, starting at bit
     * <code>aBitLength</code>. The result is returned in <code>a</code>.
     *
     * @param a
     * @param aBitLength
     * @param b
     * @param bStart
     * @param bBitLength
     */
    private static void appendBits(int[] a, int aBitLength, int[] b, int bStart, int bBitLength) {
        int aIdx = a.length - 1 - aBitLength / 32;
        int bit32 = aBitLength % 32;

        int bIdx = b.length - 1 - bStart;
        int bIdxStop = bIdx - bBitLength / 32;
        while (bIdx > bIdxStop) {
            if (bit32 > 0) {
                a[aIdx] |= b[bIdx] << bit32;
                aIdx--;
                a[aIdx] = b[bIdx] >>> (32 - bit32);
            } else {
                a[aIdx] = b[bIdx];
                aIdx--;
            }
            bIdx--;
        }

        if (bBitLength % 32 > 0) {
            int bi = b[bIdx];
            bi &= -1 >>> (32 - bBitLength % 32);
            a[aIdx] |= bi << bit32;
            if (bit32 + (bBitLength % 32) > 32) {
                a[aIdx - 1] = bi >>> (32 - bit32);
            }
        }
    }

    /**
     * Divides an <code>int</code> array into pieces <code>bitLength</code> bits long.
     *
     * @param a
     * @param bitLength
     * @return a new array containing <code>bitLength</code> bits from <code>a</code> in each subarray
     */
    private static int[][] splitBits(int[] a, int bitLength) {
        int aIntIdx = a.length - 1;
        int aBitIdx = 0;
        int numPieces = (a.length * 32 + bitLength - 1) / bitLength;
        int pieceLength = (bitLength + 31) / 32;   // in ints
        int[][] b = new int[numPieces][pieceLength];
        for (int i = 0; i < b.length; i++) {
            int bitsRemaining = Math.min(bitLength, a.length * 32 - i * bitLength);
            int bIntIdx = bitLength / 32;
            if (bitLength % 32 == 0) {
                bIntIdx--;
            }
            int bBitIdx = 0;
            while (bitsRemaining > 0) {
                int bitsToCopy = Math.min(32 - aBitIdx, 32 - bBitIdx);
                bitsToCopy = Math.min(bitsRemaining, bitsToCopy);
                int mask = a[aIntIdx] >>> aBitIdx;
                mask &= -1 >>> (32 - bitsToCopy);
                mask <<= bBitIdx;
                b[i][bIntIdx] |= mask;
                bitsRemaining -= bitsToCopy;
                aBitIdx += bitsToCopy;
                if (aBitIdx >= 32) {
                    aBitIdx -= 32;
                    aIntIdx--;
                }
                bBitIdx += bitsToCopy;
                if (bBitIdx >= 32) {
                    bBitIdx -= 32;
                    bIntIdx--;
                }
            }
        }
        return b;
    }

    /**
     * Subtracts two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo 2<sup>numBits</sup>.
     * Both input values are given as <code>int</code> arrays.
     * The result is returned in the first argument.
     *
     * @param a a number in base 2<sup>32</sup> starting with the highest digit
     * @param b a number in base 2<sup>32</sup> starting with the highest digit
     */
    private static void subModPow2(int[] a, int[] b, int numBits) {
        int numElements = (numBits + 31) / 32;
        int carry = 0;
        int i;
        int aIdx = a.length - 1;
        int bIdx = b.length - 1;
        for (i = numElements - 1; i >= 0; i--) {
            int diff = a[aIdx] - b[bIdx] - carry;
            // if signBit(diff) > signBit(a)-signBit(b) then carry=1;else carry=0
            carry = ((a[aIdx] >>> 31) - (b[bIdx] >>> 31) - (diff >>> 31)) >>> 31;
            a[aIdx] = diff;
            aIdx--;
            bIdx--;
        }
        if (numElements > 0) {
            a[aIdx + 1] &= -1 >>> (32 - (numBits % 32));
        }
        for (; aIdx >= 0; aIdx--)
            a[aIdx] = 0;
    }

    /**
     * Adds two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo 2<sup>numBits</sup>.
     * Both input values are given as <code>int</code> arrays.
     * The result is returned in the first argument.
     *
     * @param a a number in base 2<sup>32</sup> starting with the highest digit
     * @param b a number in base 2<sup>32</sup> starting with the highest digit
     */
    private static void addModPow2(int[] a, int[] b, int numBits) {
        int numElements = (numBits + 31) / 32;
        int carry = 0;
        int i;
        int aIdx = a.length - 1;
        int bIdx = b.length - 1;
        for (i = numElements - 1; i >= 0; i--) {
            int sum = a[aIdx] + b[bIdx] + carry;
            // if signBit(sum) < signBit(a)+signBit(b) then carry = 1; else carry=0
            carry = ((sum >>> 31) - (a[aIdx] >>> 31) - (b[bIdx] >>> 31)) >>> 31;
            a[aIdx] = sum;
            aIdx--;
            bIdx--;
        }
        if (numElements > 0) {
            a[aIdx + 1] &= -1 >>> (32 - (numBits % 32));
        }
        for (; aIdx >= 0; aIdx--)
            a[aIdx] = 0;
    }


    /**
     * Splits an <code>int</code> array into pieces of <code>pieceSize longs</code> each,
     * pads each piece to <code>targetPieceSize longs</code>, and wraps it in a {@link MutableModFn_tbuktu}
     * (this implies <code>targetPieceSize</code>=2<sup>k</sup>+1 for some k).
     *
     * @param a               the input array
     * @param numPieces       the number of pieces to split the array into
     * @param sourcePieceSize the size of each piece in the input array in <code>ints</code>
     * @param targetPieceSize the size of each <code>MutableModFn</code> in the output array in <code>longs</code>
     * @return an array of length <code>numPieces</code> containing {@link MutableModFn_tbuktu}s of length <code>targetPieceSize longs</code> each
     */
    private static MutableModFn_tbuktu[] split(int[] a, int numPieces, int sourcePieceSize, int targetPieceSize) {
        MutableModFn_tbuktu[] ai = new MutableModFn_tbuktu[numPieces];
        int aIdx = a.length - sourcePieceSize;
        int pieceIdx = 0;
        while (aIdx >= 0) {
            long[] digits = new long[targetPieceSize];
            if (sourcePieceSize == 1) {
                digits[targetPieceSize - 1] = (a[aIdx] & 0xFFFFFFFFL);
            } else {
                for (int i = 0; i < sourcePieceSize; i += 2)
                    digits[targetPieceSize - sourcePieceSize / 2 + i / 2] = (((long) a[aIdx + i]) << 32) | (a[aIdx + i + 1] & 0xFFFFFFFFL);
            }
            ai[pieceIdx] = new MutableModFn_tbuktu(digits);
            aIdx -= sourcePieceSize;
            pieceIdx++;
        }
        long[] digits = new long[targetPieceSize];
        if ((a.length % sourcePieceSize) % 2 == 0) {
            for (int i = 0; i < a.length % sourcePieceSize; i += 2)
                digits[targetPieceSize - (a.length % sourcePieceSize) / 2 + i / 2] = (((long) a[i]) << 32) | (a[i + 1] & 0xFFFFFFFFL);
        } else {
            for (int i = 0; i < a.length % sourcePieceSize - 2; i += 2) {
                digits[targetPieceSize - (a.length % sourcePieceSize) / 2 + i / 2] = ((long) a[i + 1]) << 32;
                digits[targetPieceSize - (a.length % sourcePieceSize) / 2 + i / 2 - 1] |= a[i] & 0xFFFFFFFFL;
            }
            // the remaining half-long
            digits[targetPieceSize - 1] |= a[a.length % sourcePieceSize - 1] & 0xFFFFFFFFL;
        }
        ai[pieceIdx] = new MutableModFn_tbuktu(digits);
        while (++pieceIdx < numPieces)
            ai[pieceIdx] = new MutableModFn_tbuktu(targetPieceSize);
        return ai;
    }

    /**
     * Performs a modified
     * <a href="http://en.wikipedia.org/wiki/Discrete_Fourier_transform_%28general%29#Number-theoretic_transform">
     * Discrete Fourier Transform</a> (a Fermat Number Transform, to be more precise) on an array whose elements
     * are <code>int</code> arrays.<br/>
     * The modification is that the first step is omitted because only the upper half of the result is needed.<br/>
     * <code>A</code> is assumed to be the lower half of the full array and the upper half is assumed to be all zeros.
     *
     * @param A        the vector to transform
     * @param omega    root of unity, can be 2 or 4
     * @param parallel number of threads to use; 1 means run on the current thread
     */
    private static void dft(MutableModFn_tbuktu[] A, int omega, boolean parallel) {
        if (parallel) {
            try {
                dftParallel(A, omega, parallel);
            } catch (InterruptedException | ExecutionException e) {
                throw new ArithmeticException(e.getLocalizedMessage());
            }
        } else {
            dftSequential(A, omega);
        }
    }

    /**
     * Performs a single-threaded DFT on {@code A}.
     * This implementation uses <a href="http://www.nas.nasa.gov/assets/pdf/techreports/1989/rnr-89-004.pdf">
     * Bailey's 4-step algorithm</a>.
     *
     * @param A     the vector to transform
     * @param omega root of unity, can be 2 or 4
     */
    private static void dftSequential(MutableModFn_tbuktu[] A, int omega) {
        // arrange the elements of A in a matrix roughly sqrt(A.length) by sqrt(A.length) in size
        int rows = 1 << ((31 - Integer.numberOfLeadingZeros(A.length)) / 2);   // number of rows
        int cols = A.length / rows;   // number of columns

        // step 1: perform an DFT on each column, that is, on the vector
        // A[colIdx], A[colIdx+cols], A[colIdx+2*cols], ..., A[colIdx+(rows-1)*cols].
        for (int i = 0; i < cols; i++)
            dftDirect(A, omega, rows, rows, cols, i, cols);

        // step 2: multiply by powers of omega
        applyDftWeights(A, omega, rows, cols);

        // step 3 is built into step 1 by making the stride length a multiple of the row length

        // step 4: perform an DFT on each row, that is, on the vector
        // A[rowIdx*cols], A[rowIdx*cols+1], ..., A[rowIdx*cols+cols-1].
        for (int i = 0; i < rows; i++)
            dftDirect(A, omega, cols, 0, rows, i * cols, 1);
    }

    /**
     * Performs a multithreaded DFT on {@code A}.
     * This implementation uses <a href="http://www.nas.nasa.gov/assets/pdf/techreports/1989/rnr-89-004.pdf">
     * Bailey's 4-step algorithm</a>.
     *
     * @param A        the vector to transform
     * @param omega    root of unity, can be 2 or 4
     * @param parallel number of threads to use; 1 means run on the current thread
     */
    private static void dftParallel(final MutableModFn_tbuktu[] A, final int omega, boolean parallel) throws InterruptedException, ExecutionException {
        // arrange the elements of A in a matrix roughly sqrt(A.length) by sqrt(A.length) in size
        final int rows = 1 << ((31 - Integer.numberOfLeadingZeros(A.length)) / 2);   // number of rows
        final int cols = A.length / rows;   // number of columns

        ExecutorService executor = ForkJoinPool.commonPool();

        // step 1: perform an DFT on each column
        Collection<Future<?>> pending = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            final int colIdx = i;
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    dftDirect(A, omega, rows, rows, cols, colIdx, cols);
                }
            });
            pending.add(future);
        }
        for (Future<?> future : pending)
            future.get();

        // step 2: multiply by powers of omega
        applyDftWeights(A, omega, rows, cols);

        // step 3 is built into step 1 by making the stride length a multiple of the row length

        // step 4: perform an DFT on each row
        pending = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            final int rowIdx = i;
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    dftDirect(A, omega, cols, 0, rows, rowIdx * cols, 1);
                }
            });
            pending.add(future);
        }
        for (Future<?> future : pending)
            future.get();

        executor.shutdown();
    }

    /**
     * Performs a DFT on {@code A}.
     * This implementation uses the radix-4 technique which combines two levels of butterflies.
     *
     * @param A         the vector to transform
     * @param omega     root of unity, can be 2 or 4
     * @param expOffset value to add to the array index when computing the exponent
     * @param expScale  factor by which to multiply the exponent
     * @param len       number of elements to transform
     * @param idxOffset value to add to the array index when accessing elements of {@code A}
     * @param stride    stride length
     */
    private static void dftDirect(MutableModFn_tbuktu[] A, int omega, int len, int expOffset, int expScale, int idxOffset, int stride) {
        int n = 31 - Integer.numberOfLeadingZeros(2 * len);   // multiply by 2 because we're doing a half DFT and we need the n that corresponds to the full DFT length
        int v = 1;   // v starts at 1 rather than 0 for the same reason
        MutableModFn_tbuktu d = new MutableModFn_tbuktu(A[0].digits.length);

        int slen = len / 2;
        while (slen > 1) {   // slen = #consecutive coefficients for which the sign (add/sub) and x are constant
            for (int j = 0; j < len; j += 2 * slen) {
                int x1 = getDftExponent(n, v + 1, j + expOffset, omega) * expScale;        // for level v+2
                int x2 = getDftExponent(n, v, j + expOffset, omega) * expScale;          // for level v+1
                int x3 = getDftExponent(n, v + 1, j + slen + expOffset, omega) * expScale;   // for level v+2

                // stride length = stride*slen elements
                int idx0 = stride * j + idxOffset;
                int idx1 = stride * j + stride * slen / 2 + idxOffset;
                int idx2 = idx0 + stride * slen;
                int idx3 = idx1 + stride * slen;

                for (int k = slen - 1; k >= 0; k -= 2) {
                    // do level v+1
                    A[idx2].shiftLeft(x2, d);
                    A[idx0].copyTo(A[idx2]);
                    A[idx0].add(d);
                    A[idx2].subtract(d);

                    A[idx3].shiftLeft(x2, d);
                    A[idx1].copyTo(A[idx3]);
                    A[idx1].add(d);
                    A[idx3].subtract(d);

                    // do level v+2
                    A[idx1].shiftLeft(x1, d);
                    A[idx0].copyTo(A[idx1]);
                    A[idx0].add(d);
                    A[idx1].subtract(d);

                    A[idx3].shiftLeft(x3, d);
                    A[idx2].copyTo(A[idx3]);
                    A[idx2].add(d);
                    A[idx3].subtract(d);

                    idx0 += stride;
                    idx1 += stride;
                    idx2 += stride;
                    idx3 += stride;
                }
            }

            v += 2;
            slen /= 4;
        }

        // if there is an odd number of levels, do the remaining one now
        if (slen > 0) {
            for (int j = 0; j < len; j += 2 * slen) {
                int x = getDftExponent(n, v, j + expOffset, omega) * expScale;
                int idx = stride * j + idxOffset;
                int idx2 = idx + stride * slen;   // stride length = stride*slen elements

                for (int k = slen - 1; k >= 0; k--) {
                    A[idx2].shiftLeft(x, d);
                    A[idx].copyTo(A[idx2]);
                    A[idx].add(d);
                    A[idx2].subtract(d);
                    idx += stride;
                    idx2 += stride;
                }
            }
        }
    }

    /**
     * Returns the power to which to raise omega in a DFT.<br/>
     * When <code>omega</code>=4, this method doubles the exponent so
     * <code>omega</code> can be assumed always to be 2 in the
     * {@code dftDirect} and {@code idftDirect} methods.
     *
     * @param n     the log of the DFT length
     * @param v     butterfly depth
     * @param idx   index of the array element to be computed
     * @param omega root of unity, can be 2 or 4
     * @return
     */
    private static int getDftExponent(int n, int v, int idx, int omega) {
        // x = 2^(n-1-v) * s, where s is the v (out of n) high bits of idx in reverse order
        int x = Integer.reverse(idx >>> (n - v)) >>> (32 - v);
        x <<= n - v - 1;

        // if omega=4, double the shift amount
        if (omega == 4) {
            x *= 2;
        }

        return x;
    }

    /**
     * Multiplies vector elements by powers of omega (aka twiddle factors). Used by Bailey's algorithm.
     *
     * @param A     the vector to transform
     * @param omega root of unity, can be 2 or 4
     * @param rows  number of matrix rows
     * @param cols  number of matrix columns
     */
    private static void applyDftWeights(MutableModFn_tbuktu[] A, int omega, int rows, int cols) {
        int v = 31 - Integer.numberOfLeadingZeros(rows) + 1;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                int idx = i * cols + j;
                MutableModFn_tbuktu temp = new MutableModFn_tbuktu(A[idx].digits.length);
                int shiftAmt = getBaileyShiftAmount(i, j, rows, v);
                if (omega == 4) {
                    shiftAmt *= 2;
                }
                A[idx].shiftLeft(shiftAmt, temp);
                System.arraycopy(temp.digits, 0, A[idx].digits, 0, temp.digits.length);
            }
    }

    private static int getBaileyShiftAmount(int i, int j, int rows, int v) {
        int iRev = Integer.reverse(i + rows) >>> (32 - v);
        return iRev * j;
    }

    /**
     * Calls {@code square()} for each element of <code>a</code> and places the result into
     * <code>a</code>, i.e., <code>a[i]</code> becomes <code>a[i]<sup>2</sup></code> for all
     * <code>i</code>.
     *
     * @param a
     * @param parallel whether to run in parallel
     */
    private static void squareElements(final MutableModFn_tbuktu[] a, boolean parallel) {
        if (parallel) {
            ForkJoinPool executor = ForkJoinPool.commonPool();
            int numThreads = executor.getParallelism();
            Collection<Future<?>> pending = new ArrayList<>();
            for (int i = 0; i < numThreads; i++) {
                final int fromIdx = a.length * i / numThreads;
                final int toIdx = a.length * (i + 1) / numThreads;
                Future<?> future = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int idx = fromIdx; idx < toIdx; idx++)
                            a[idx].square(parallel);
                    }
                });
                pending.add(future);
            }
            try {
                for (Future<?> future : pending)
                    future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ArithmeticException(e.getLocalizedMessage());
            }
        } else {
            for (int i = 0; i < a.length; i++)
                a[i].square(parallel);
        }
    }

    /**
     * Calls {@code multiply()} for each element of <code>a</code> and <code>b</code> and
     * places the result into <code>a</code>, i.e., <code>a[i]</code> becomes
     * <code>a[i]*b[i]</code> for all <code>i</code>.
     *
     * @param a
     * @param b        an array of the same length as <code>a</code>
     * @param parallel number of threads to use; 1 means run on the current thread
     */
    private static void multiplyElements(final MutableModFn_tbuktu[] a, final MutableModFn_tbuktu[] b, boolean parallel) {
        if (parallel) {
            ForkJoinPool executor = ForkJoinPool.commonPool();
            int numThreads = executor.getParallelism();
            Collection<Future<?>> pending = new ArrayList<>();
            for (int i = 0; i < numThreads; i++) {
                final int fromIdx = a.length * i / numThreads;
                final int toIdx = a.length * (i + 1) / numThreads;
                Future<?> future = executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int idx = fromIdx; idx < toIdx; idx++)
                            a[idx].multiply(b[idx], parallel);
                    }
                });
                pending.add(future);
            }
            try {
                for (Future<?> future : pending)
                    future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new ArithmeticException(e.getLocalizedMessage());
            }
        } else {
            for (int i = 0; i < a.length; i++)
                a[i].multiply(b[i], parallel);
        }
    }


    /**
     * Performs a modified
     * <a href="http://en.wikipedia.org/wiki/Discrete_Fourier_transform_%28general%29#Number-theoretic_transform">
     * Inverse Fermat Number Transform</a> on an array whose elements are <code>int</code> arrays.
     * The modification is that the last step (the one where the upper half is subtracted from the lower half)
     * is omitted.<br/>
     * <code>A</code> is assumed to be the upper half of the full array and the lower half is assumed to be all zeros.
     *
     * @param A        the vector to transform
     * @param omega    root of unity, can be 2 or 4
     * @param parallel number of threads to use; 1 means run on the current thread
     */
    private static void idft(MutableModFn_tbuktu[] A, int omega, boolean parallel) {
        if (parallel) {
            try {
                idftParallel(A, omega, parallel);
            } catch (InterruptedException | ExecutionException e) {
                throw new ArithmeticException(e.getLocalizedMessage());
            }
        } else {
            idftSequential(A, omega);
        }
    }

    /**
     * Performs a single-threaded IDFT on {@code A}.
     * This implementation uses <a href="http://www.nas.nasa.gov/assets/pdf/techreports/1989/rnr-89-004.pdf">
     * Bailey's 4-step algorithm</a>.
     *
     * @param A     the vector to transform
     * @param omega root of unity, can be 2 or 4
     */
    private static void idftSequential(MutableModFn_tbuktu[] A, int omega) {
        // arrange the elements of A in a matrix roughly sqrt(A.length) by sqrt(A.length) in size
        int rows = 1 << ((31 - Integer.numberOfLeadingZeros(A.length)) / 2);   // number of rows
        int cols = A.length / rows;   // number of columns

        // step 1: perform an IDFT on each row, that is, on the vector
        // A[rowIdx*cols], A[rowIdx*cols+1], ..., A[rowIdx*cols+cols-1].
        for (int i = 0; i < rows; i++)
            idftDirect(A, omega, cols, 0, rows, i * cols, 1);

        // step 2: multiply by powers of omega
        applyIdftWeights(A, omega, rows, cols);

        // step 3 is built into step 4 by making the stride length a multiple of the row length

        // step 4: perform an IDFT on each column, that is, on the vector
        // A[colIdx], A[colIdx+cols], A[colIdx+2*cols], ..., A[colIdx+(rows-1)*cols].
        for (int i = 0; i < cols; i++)
            idftDirect(A, omega, rows, rows, cols, i, cols);
    }

    /**
     * Performs a multithreaded IDFT on {@code A}.
     * This implementation uses <a href="http://www.nas.nasa.gov/assets/pdf/techreports/1989/rnr-89-004.pdf">
     * Bailey's 4-step algorithm</a>.
     *
     * @param A          the vector to transform
     * @param omega      root of unity, can be 2 or 4
     * @param numThreads number of threads to use; 1 means run on the current thread
     */
    private static void idftParallel(final MutableModFn_tbuktu[] A, final int omega, boolean numThreads) throws InterruptedException, ExecutionException {
        // arrange the elements of A in a matrix roughly sqrt(A.length) by sqrt(A.length) in size
        final int rows = 1 << ((31 - Integer.numberOfLeadingZeros(A.length)) / 2);   // number of rows
        final int cols = A.length / rows;   // number of columns

        ForkJoinPool executor = ForkJoinPool.commonPool();

        // step 1: perform an IDFT on each row
        Collection<Future<?>> pending = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            final int rowIdx = i;
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    idftDirect(A, omega, cols, 0, rows, rowIdx * cols, 1);
                }
            });
            pending.add(future);
        }
        for (Future<?> future : pending)
            future.get();

        // step 2: multiply by powers of omega
        applyIdftWeights(A, omega, rows, cols);

        // step 3 is built into step 4 by making the stride length a multiple of the row length
        // step 4: perform an IDFT on each column
        pending = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            final int colIdx = i;
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    idftDirect(A, omega, rows, rows, cols, colIdx, cols);
                }
            });
            pending.add(future);
        }
        for (Future<?> future : pending)
            future.get();

    }

    /**
     * This implementation uses the radix-4 technique which combines two levels of butterflies.
     */
    private static void idftDirect(MutableModFn_tbuktu[] A, int omega, int len, int expOffset, int expScale, int idxOffset, int stride) {
        int n = 31 - Integer.numberOfLeadingZeros(2 * len);   // multiply by 2 because we're doing a half DFT and we need the n that corresponds to the full DFT length
        int v = 31 - Integer.numberOfLeadingZeros(len);
        MutableModFn_tbuktu c = new MutableModFn_tbuktu(A[0].digits.length);

        int slen = 1;
        while (slen <= len / 4) {   // slen = #consecutive coefficients for which the sign (add/sub) and x are constant
            for (int j = 0; j < len; j += 4 * slen) {
                int x1 = getDftExponent(n, v, j + expOffset, omega) * expScale + 1;          // for level v
                int x2 = getDftExponent(n, v - 1, j + expOffset, omega) * expScale + 1;        // for level v-1
                int x3 = getDftExponent(n, v, j + slen * 2 + expOffset, omega) * expScale + 1;   // for level v

                // stride length = stride*slen elements
                int idx0 = stride * j + idxOffset;
                int idx1 = stride * j + stride * slen + idxOffset;
                int idx2 = idx0 + stride * slen * 2;
                int idx3 = idx1 + stride * slen * 2;

                for (int k = slen - 1; k >= 0; k--) {
                    // do level v
                    A[idx0].copyTo(c);
                    A[idx0].add(A[idx1]);
                    A[idx0].shiftRight(1, A[idx0]);
                    c.subtract(A[idx1]);
                    c.shiftRight(x1, A[idx1]);

                    A[idx2].copyTo(c);
                    A[idx2].add(A[idx3]);
                    A[idx2].shiftRight(1, A[idx2]);
                    c.subtract(A[idx3]);
                    c.shiftRight(x3, A[idx3]);

                    // do level v-1
                    A[idx0].copyTo(c);
                    A[idx0].add(A[idx2]);
                    A[idx0].shiftRight(1, A[idx0]);
                    c.subtract(A[idx2]);
                    c.shiftRight(x2, A[idx2]);

                    A[idx1].copyTo(c);
                    A[idx1].add(A[idx3]);
                    A[idx1].shiftRight(1, A[idx1]);
                    c.subtract(A[idx3]);
                    c.shiftRight(x2, A[idx3]);

                    idx0 += stride;
                    idx1 += stride;
                    idx2 += stride;
                    idx3 += stride;
                }
            }

            v -= 2;
            slen *= 4;
        }

        // if there is an odd number of levels, do the remaining one now
        if (slen <= len / 2) {
            for (int j = 0; j < len; j += 2 * slen) {
                int x = getDftExponent(n, v, j + expOffset, omega) * expScale + 1;
                int idx = stride * j + idxOffset;
                int idx2 = idx + stride * slen;   // stride length = stride*slen elements

                for (int k = slen - 1; k >= 0; k--) {
                    A[idx].copyTo(c);
                    A[idx].add(A[idx2]);
                    A[idx].shiftRight(1, A[idx]);

                    c.subtract(A[idx2]);
                    c.shiftRight(x, A[idx2]);
                    idx += stride;
                    idx2 += stride;
                }
            }
        }
    }

    /**
     * Divides vector elements by powers of omega (aka twiddle factors)
     */
    private static void applyIdftWeights(MutableModFn_tbuktu[] A, int omega, int rows, int cols) {
        int v = 31 - Integer.numberOfLeadingZeros(rows) + 1;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                int idx = i * cols + j;
                MutableModFn_tbuktu temp = new MutableModFn_tbuktu(A[idx].digits.length);
                int shiftAmt = getBaileyShiftAmount(i, j, rows, v);
                if (omega == 4) {
                    shiftAmt *= 2;
                }
                A[idx].shiftRight(shiftAmt, temp);
                temp.copyTo(A[idx]);
            }
    }

}
