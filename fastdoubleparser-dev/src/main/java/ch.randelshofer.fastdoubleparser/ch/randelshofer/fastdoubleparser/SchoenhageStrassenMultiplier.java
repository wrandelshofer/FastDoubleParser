/*
 * @(#)SchoenhagenStrassenMultiplier_robbymckilliam.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Provides methods for multiplying two {@link BigInteger}s using the
 * {@code Schönhage-Strassen algorithm}.
 * <p>
 * This code is based on {@code bigint} by Timothy Buktu and Robby McKilliam.
 * <p>
 * References:
 * <dl>
 *     <dt>Schönhage-Strassen algorithm
 *     </dt>
 *     <dd><a href="http://en.wikipedia.org/wiki/Sch%C3%B6nhage%E2%80%93Strassen_algorithm">
 *  wikipedia</a>.</dd>
 *
 *     <dt>bigint, Copyright 2013 © Timothy Buktu, 2-Clause BSD License.
 *     Forked by Robby McKilliam.
 *     </dt>
 *     <dd><a href="https://github.com/robbymckilliam/bigint">github.com</a></dd>
 * </dl>
 */
class SchoenhageStrassenMultiplier {
    /**
     * Don't let anyone instantiate this class.
     */
    private SchoenhageStrassenMultiplier() {
    }

    private static final int SCHOENHAGEN_STRASSEN_THRESHOLD = 1249000;

    /**
     * Adds two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo 2^2^n+1.
     * The number n is <code>a.length*32/2</code>; in other words, n is half the number of bits in
     * <code>a</code>.<br/>
     * Both input values are given as <code>int</code> arrays; they must be the same length.
     * The result is returned in the first argument.
     *
     * @param a a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     * @param b a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     */
    private static void addModFn(int[] a, int[] b) {
        int carry = 0;
        for (int i = 0; i < a.length; i++) {
            int sum = a[i] + b[i] + carry;
            // if signBit(sum) < signBit(a[i]) + signBit(b[i]) then carry = 1; else carry=0
            carry = ((sum >>> 31) - (a[i] >>> 31) - (b[i] >>> 31)) >>> 31;
            a[i] = sum;
        }

        // take a mod Fn by adding any remaining carry bit to the lowest bit;
        // since Fn ≡ 1 (mod 2^n), it suffices to add 1
        int i = 0;
        boolean carryFlag = carry != 0;
        while (carryFlag) {
            int sum = a[i] + 1;
            a[i] = sum;
            carryFlag = sum == 0;
            i++;
            if (i >= a.length) {
                i = 0;
            }
        }
    }

    /**
     * Adds two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo 2^numBits.
     * Both input values are given as <code>int</code> arrays.
     * The result is returned in the first argument.
     *
     * @param a a number in base 2^32 starting with the lowest digit
     * @param b a number in base 2^32 starting with the lowest digit
     */
    private static void addModPow2(int[] a, int[] b, int numBits) {
        int numElements = (numBits + 31) / 32;
        int carry = 0;
        int i;
        for (i = 0; i < numElements; i++) {
            int sum = a[i] + b[i] + carry;
            // if signBit(sum) < signBit(a)+signBit(b) then carry = 1; else carry=0
            carry = ((sum >>> 31) - (a[i] >>> 31) - (b[i] >>> 31)) >>> 31;
            a[i] = sum;
        }
        a[i - 1] &= -1 >>> (32 - (numBits % 32));
        for (; i < a.length; i++)
            a[i] = 0;
    }

    /**
     * Adds two numbers, <code>a</code> and <code>b</code>, after shifting <code>b</code> by
     * <code>numElements</code> elements.<br/>
     * Both numbers are given as <code>int</code> arrays and must be <b>positive</b> numbers
     * (meaning they are interpreted as unsigned).</br> The result is returned in the first
     * argument.
     * If any elements of b are shifted outside the valid range for <code>a</code>, they are dropped.
     *
     * @param a           a number in base 2^32 starting with the lowest digit
     * @param b           a number in base 2^32 starting with the lowest digit
     * @param numElements
     */
    private static void addShifted(int[] a, int[] b, int numElements) {
        int carry = 0;
        int i = 0;
        while (i < Math.min(b.length, a.length - numElements)) {
            int ai = a[i + numElements];
            int sum = ai + b[i] + carry;
            // if signBit(sum) < signBit(a)+signBit(b) then carry = 1; else carry=0
            carry = ((sum >>> 31) - (ai >>> 31) - (b[i] >>> 31)) >>> 31;
            a[i + numElements] = sum;
            i++;
        }
        boolean carryFlag = carry != 0;
        while (carryFlag) {
            a[i + numElements]++;
            carryFlag = a[i + numElements] == 0;
            i++;
        }
    }

    /**
     * Reads <code>bBitLength</code> bits from <code>b</code>, starting at array index
     * <code>bStart</code>, and copies them into <code>a</code>, starting at bit
     * <code>aBitLength</code>. The result is returned in <code>a</code>.
     */
    private static void appendBits(int[] a, int aBitLength, int[] b, int bStart, int bBitLength) {
        int aIdx = aBitLength / 32;
        int bit32 = aBitLength % 32;

        for (int i = bStart; i < bStart + bBitLength / 32; i++) {
            if (bit32 > 0) {
                a[aIdx] |= b[i] << bit32;
                aIdx++;
                a[aIdx] = b[i] >>> (32 - bit32);
            } else {
                a[aIdx] = b[i];
                aIdx++;
            }
        }

        if (bBitLength % 32 > 0) {
            int bIdx = bBitLength / 32;
            int bi = b[bStart + bIdx];
            bi &= -1 >>> (32 - bBitLength);
            a[aIdx] |= bi << bit32;
            if (bit32 + (bBitLength % 32) > 32) {
                a[aIdx + 1] = bi >>> (32 - bit32);
            }
        }
    }

    /**
     * Shifts a number to the left modulo 2^2^n+1 and returns the result in a new array.
     * "Left" means towards the lower array indices and the lower bits; this is equivalent to
     * a multiplication by 2^numBits modulo 2^2^n+1.<br/>
     * The number n is <code>a.length*32/2</code>; in other words, n is half the number of bits in
     * <code>a</code>.<br/>
     * Both input values are given as <code>int</code> arrays; they must be the same length.
     * The result is returned in the first argument.
     *
     * @param a       a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     * @param numBits the shift amount in bits
     * @return the shifted number
     */
    private static int[] cyclicShiftLeftBits(int[] a, int numBits) {
        int[] b = cyclicShiftLeftElements(a, numBits / 32);

        numBits = numBits % 32;
        if (numBits != 0) {
            int bhi = b[b.length - 1];
            b[b.length - 1] <<= numBits;
            for (int i = b.length - 1; i > 0; i--) {
                b[i] |= b[i - 1] >>> (32 - numBits);
                b[i - 1] <<= numBits;
            }
            b[0] |= bhi >>> (32 - numBits);
        }
        return b;
    }

    /**
     * Cyclicly shifts an array towards the higher indices by <code>numElements</code>
     * elements and returns the result in a new array.
     *
     * @param a
     * @param numElements
     * @return
     */
    private static int[] cyclicShiftLeftElements(int[] a, int numElements) {
        int[] b = new int[a.length];
        System.arraycopy(a, 0, b, numElements, a.length - numElements);
        System.arraycopy(a, a.length - numElements, b, 0, numElements);
        return b;
    }

    /**
     * Cyclicly shifts a number to the right modulo 2^2^n+1 and returns the result in a new array.
     * "Right" means towards the lower array indices and the lower bits; this is equivalent to
     * a multiplication by 2^(-numBits) modulo 2^2^n+1.<br/>
     * The number n is <code>a.length*32/2</code>; in other words, n is half the number of bits in
     * <code>a</code>.<br/>
     * Both input values are given as <code>int</code> arrays; they must be the same length.
     * The result is returned in the first argument.
     *
     * @param a       a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     * @param numBits the shift amount in bits
     * @return the shifted number
     */
    private static int[] cyclicShiftRight(int[] a, int numBits) {
        int[] b = new int[a.length];
        int numElements = numBits / 32;
        System.arraycopy(a, numElements, b, 0, a.length - numElements);
        System.arraycopy(a, 0, b, a.length - numElements, numElements);

        numBits = numBits % 32;
        if (numBits != 0) {
            int b0 = b[0];
            b[0] = b[0] >>> numBits;
            for (int i = 1; i < b.length; i++) {
                b[i - 1] |= b[i] << (32 - numBits);
                b[i] = b[i] >>> numBits;
            }
            b[b.length - 1] |= b0 << (32 - numBits);
        }
        return b;
    }

    /**
     * Performs a
     * <a href="http://en.wikipedia.org/wiki/Discrete_Fourier_transform_%28general%29#Number-theoretic_transform">
     * Fermat Number Transform</a> on an array whose elements are <code>int</code> arrays.<br/>
     * <code>A</code> is assumed to be the lower half of the full array and the upper half is assumed to be all zeros.
     * The number of subarrays in <code>A</code> must be 2^n if m is even and 2^(n+1) if m is odd.<br/>
     * Each subarray must be ceil(2^(n-1)) bits in length.<br/>
     * n must be equal to m/2-1.
     *
     * @param A
     * @param m
     * @param n
     */
    private static void dft(int[][] A, int m, int n) {
        boolean even = m % 2 == 0;
        int len = A.length;
        int v = 1;

        for (int slen = len / 2; slen > 0; slen /= 2) {   // slen = #consecutive coefficients for which the sign (add/sub) and x are constant
            for (int j = 0; j < len; j += 2 * slen) {
                int idx = j;
                int x = getDftExponent(n, v, idx + len, even);

                for (int k = slen - 1; k >= 0; k--) {
                    int[] d = cyclicShiftLeftBits(A[idx + slen], x);
                    //int[] c = A[idx].clone();
                    System.arraycopy(A[idx], 0, A[idx + slen], 0, A[idx].length);   // copy A[idx] into A[idx+slen]
                    addModFn(A[idx], d);
                    //subModFn(c, d, 1<<n);
                    //A[idx+slen] = c;
                    subModFn(A[idx + slen], d, 1 << n);
                    idx++;
                }
            }

            v++;
        }
    }

    /**
     * Returns the power to which to raise omega in a DFT.<br/>
     * Omega itself is either 2 or 4 depending on m, but when omega=4 this method
     * doubles the exponent so omega can be assumed always to be 2 in a DFT.
     *
     * @param n
     * @param v
     * @param idx
     * @param even
     * @return
     */
    private static int getDftExponent(int n, int v, int idx, boolean even) {
        // take bits n-v..n-1 of idx, reverse them, shift left by n-v-1
        int x = Integer.reverse(idx) << (n - v) >>> (31 - n);

        // if m is even, divide by two
        if (even) {
            x >>>= 1;
        }

        return x;
    }

    /**
     * Returns the power to which to raise omega in an IDFT.<br/>
     * Omega itself is either 2 or 4 depending on m, but when omega=4 this method
     * doubles the exponent so omega can be assumed always to be 2 in a IDFT.
     *
     * @param n
     * @param v
     * @param idx
     * @param even
     * @return
     */
    private static int getIdftExponent(int n, int v, int idx, boolean even) {
        int x = Integer.reverse(idx) << (n - v) >>> (32 - n);
        x += even ? 1 << (n - v) : 1 << (n - 1 - v);
        return x + 1;
    }

    /**
     * Performs a modified
     * <a href="http://en.wikipedia.org/wiki/Discrete_Fourier_transform_%28general%29#Number-theoretic_transform">
     * Inverse Fermat Number Transform</a> on an array whose elements are <code>int</code> arrays.
     * The modification is that the last step (the one where the upper half is subtracted from the lower half)
     * is omitted.<br/>
     * <code>A</code> is assumed to be the upper half of the full array and the upper half is assumed to be all zeros.
     * The number of subarrays in <code>A</code> must be 2^n if m is even and 2^(n+1) if m is odd.<br/>
     * Each subarray must be ceil(2^(n-1)) bits in length.<br/>
     * n must be equal to m/2-1.
     *
     * @param A
     * @param m
     * @param n
     */
    private static void idft(int[][] A, int m, int n) {
        boolean even = m % 2 == 0;
        int len = A.length;
        int v = n - 1;
        int[] c = new int[A[0].length];

        for (int slen = 1; slen <= len / 2; slen *= 2) {   // slen = #consecutive coefficients for which the sign (add/sub) and x are constant
            for (int j = 0; j < len; j += 2 * slen) {
                int idx = j;
                int idx2 = idx + slen;   // idx2 is always idx+slen
                int x = getIdftExponent(n, v, idx, even);

                for (int k = slen - 1; k >= 0; k--) {
                    //int[] c = A[idx].clone();
                    System.arraycopy(A[idx], 0, c, 0, c.length);   // copy A[idx] into c
                    addModFn(A[idx], A[idx2]);
                    A[idx] = cyclicShiftRight(A[idx], 1);

                    subModFn(c, A[idx2], 1 << n);
                    A[idx2] = cyclicShiftRight(c, x);
                    idx++;
                    idx2++;
                }
            }

            v--;
        }
    }

    private static void modFn(int[] a) {
        int len = a.length;
        int carry = 0;
        for (int i = 0; i < len / 2; i++) {
            int bi = a[len / 2 + i];
            int diff = a[i] - bi - carry;
            // if signBit(diff) > signBit(a)-signBit(b) then carry = 1; else carry=0
            carry = ((a[i] >>> 31) - (bi >>> 31) - (diff >>> 31)) >>> 31;
            a[i] = diff;
        }
        for (int i = len / 2; i < len; i++) {
            a[i] = 0;
        }
        // if result is negative, add Fn; since Fn ≡ 1 (mod 2^n), it suffices to add 1
        boolean carryFlag = carry != 0;
        int j = 0;
        while (carryFlag) {
            int sum = a[j] + 1;
            a[j] = sum;
            carryFlag = sum == 0;
            j++;
            if (j >= a.length) {
                j = 0;
            }
        }
    }

    /**
     * Reduces all subarrays modulo 2^2^n+1 where n=<code>a[i].length*32/2</code> for all i;
     * in other words, n is half the number of bits in the subarray.
     *
     * @param a int arrays whose length is a power of 2
     */
    private static void modFn(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            modFn(a[i]);
        }
    }

    /**
     * Multiplies two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo Fn
     * where Fn=2^2^n+1, and returns the result in a new array.<br/>
     * <code>a</code> and <code>b</code> are assumed to be reduced mod Fn, i.e. 0<=a<Fn and 0<=b<Fn.
     * The number n is <code>a.length*32/2</code>; in other words, n is half the number of bits in
     * <code>a</code>.<br/>
     * Both input values are given as <code>int</code> arrays; they must be the same length.
     *
     * @param a        a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     * @param b        a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     * @param parallel
     */
    private static int[] multModFn(int[] a, int[] b, boolean parallel) {
        int[] a0 = Arrays.copyOf(a, a.length / 2);
        int[] b0 = Arrays.copyOf(b, b.length / 2);
        int[] c = multReverse(a0, b0, parallel);
        c = Arrays.copyOf(c, a.length);   // make sure c is the same length as a and b so subModFn uses the right n
        int n = a.length / 2;
        // special case: if a=Fn-1, add b*2^2^n which is the same as subtracting b
        if (a[n] == 1) {
            subModFn(c, Arrays.copyOf(b0, c.length), n * 32);
        }
        if (b[n] == 1) {
            subModFn(c, Arrays.copyOf(a0, c.length), n * 32);
        }
        return c;
    }

    /**
     * Multiplies two <b>positive</b> numbers represented as int arrays, i.e. in base <code>2^32</code>.
     * Positive means an int is always interpreted as an unsigned number, regardless of the sign bit.<br/>
     * The arrays must be ordered least significant to most significant, so the least significant digit
     * must be at index 0.
     */
    private static int[] multReverse(int[] a, int[] b, boolean parallel) {
        BigInteger aBigInt = FastIntegerMath.newBigInteger(1, reverse(a));
        BigInteger bBigInt = FastIntegerMath.newBigInteger(1, reverse(b));
        return reverse(FastIntegerMath.getMagnitude(multiply(aBigInt, bBigInt, parallel)));
    }

    /**
     * Returns a BigInteger whose value is {@code (a * b)}.
     *
     * @param a        value a
     * @param b        value b
     * @param parallel whether to multiply in parallel
     * @return {@code a * b}
     */
    public static BigInteger multiply(BigInteger a, BigInteger b, boolean parallel) {
        if (a.signum() == 0 || b.signum() == 0) {
            return BigInteger.ZERO;
        }

        int alen = a.bitLength();
        int blen = b.bitLength();
        if (shouldMultiplySchoenhageStrassen(alen) && shouldMultiplySchoenhageStrassen(blen)) {
            return multiplySchoenhageStrassen(a, b, parallel);
        } else {
            return FastIntegerMath.parallelMultiply(a, b, parallel);
        }
    }

    /**
     * Multiplies two {@link BigInteger}s using the
     * <a href="http://en.wikipedia.org/wiki/Sch%C3%B6nhage%E2%80%93Strassen_algorithm">
     * Schönhage-Strassen algorithm</a> algorithm.
     *
     * @param a        value a
     * @param b        value b
     * @param parallel whether to multiply in parallel
     * @return a <code>BigInteger</code> equal to <code>a.multiply(b)</code>
     */
    public static BigInteger multiplySchoenhageStrassen(BigInteger a, BigInteger b, boolean parallel) {
        // remove any minus signs, multiply, then fix sign
        int signum = a.signum() * b.signum();
        if (a.signum() < 0) {
            a = a.negate();
        }
        if (b.signum() < 0) {
            b = b.negate();
        }

        // make reverse-order copies of a.mag and b.mag
        int[] aIntArr = reverse(FastIntegerMath.getMagnitude(a));
        int[] bIntArr = reverse(FastIntegerMath.getMagnitude(b));

        int[] cIntArr = multiplySchoenhageStrassen(aIntArr, a.bitLength(), bIntArr, b.bitLength(), parallel);

        return FastIntegerMath.newBigInteger(signum, reverse(cIntArr));
    }

    /**
     * This is the core Schönhage-Strassen method. It multiplies two <b>positive</b> numbers of length
     * <code>aBitLen</code> and </code>bBitLen</code> that are represented as int arrays, i.e. in base 2^32.
     * Positive means an int is always interpreted as an unsigned number, regardless of the sign bit.<br/>
     * The arrays must be ordered least significant to most significant, so the least significant digit
     * must be at index 0.
     * <p/>
     * The Schönhage-Strassen algorithm algorithm works as follows:
     * <ol>
     *   <li>Given numbers a and b, split both numbers into pieces of length 2^(n-1) bits.</li>
     *   <li>Take the low n+2 bits of each piece of a, zero-pad them to 3n+5 bits,
     *       and concatenate them to a new number u.</li>
     *   <li>Do the same for b to obtain v.</li>
     *   <li>Calculate all pieces of z' by multiplying u and v (using Schönhage-Strassen or another
     *       algorithm). The product will contain all pieces of a*b mod n+2.</li>
     *   <li>Pad the pieces of a and b from step 1 to 2^(n+1) bits.</li>
     *   <li>Perform a
     *       <a href="http://en.wikipedia.org/wiki/Discrete_Fourier_transform_%28general%29#Number-theoretic_transform">
     *       Discrete Fourier Transform</a> (DFT) on the padded pieces.</li>
     *   <li>Calculate all pieces of z" by multiplying the i-th piece of a by the i-th piece of b.</li>
     *   <li>Perform an Inverse Discrete Fourier Transform (IDFT) on z". z" will contain all pieces of
     *       a*b mod Fn where Fn=2^2^n+1.</li>
     *   <li>Calculate all pieces of z such that each piece is congruent to z' modulo n+2 and congruent to
     *       z" modulo Fn. This is done using the
     *       <a href="http://en.wikipedia.org/wiki/Chinese_remainder_theorem">Chinese remainder theorem</a>.</li>
     *   <li>Calculate c by adding z_i * 2^(i*2^(n-1)) for all i, where z_i is the i-th piece of z.</li>
     *   <li>Return c reduced modulo 2^2^m+1.</li>
     * </ol>
     * <p>
     * References:
     * <ol>
     *   <li><a href="http://en.wikipedia.org/wiki/Sch%C3%B6nhage%E2%80%93Strassen_algorithm">
     *       Wikipedia articla</a>
     *   <li><a href="http://www.scribd.com/doc/68857222/Schnelle-Multiplikation-gro%C3%9Fer-Zahlen">
     *       Arnold Schönhage und Volker Strassen: Schnelle Multiplikation großer Zahlen, Computing 7, 1971,
     *       Springer-Verlag, S. 281–292</a></li>
     *   <li><a href="http://malte-leip.net/beschreibung_ssa.pdf">Eine verständliche Beschreibung des
     *       Schönhage-Strassen-Algorithmus</a></li>
     * </ol>
     *
     * @param a
     * @param aBitLen
     * @param b
     * @param bBitLen
     * @param parallel
     * @return a*b
     */
    private static int[] multiplySchoenhageStrassen(int[] a, int aBitLen, int[] b, int bBitLen, boolean parallel) {
        // set M to the number of binary digits in a or b, whichever is greater
        int M = Math.max(aBitLen, bBitLen);

        // find the lowest m such that m>=log2(2M)
        int m = 32 - Integer.numberOfLeadingZeros(2 * M - 1 - 1);

        int n = m / 2 + 1;

        // split a and b into pieces 1<<(n-1) bits long; assume n>=6 so pieces start and end at int boundaries
        boolean even = m % 2 == 0;
        int numPieces = even ? 1 << n : 1 << (n + 1);
        assert n >= 6 : "n=" + n + " is too small";
        int pieceSize = 1 << (n - 1 - 5);   // in ints

        // build u and v from a and b, allocating 3n+5 bits in u and v per n+2 bits from a and b, resp.
        int numPiecesA = (a.length + pieceSize) / pieceSize;
        int[] u = new int[(numPiecesA * (3 * n + 5) + 31) / 32];
        int uBitLength = 0;
        for (int i = 0; i < numPiecesA && i * pieceSize < a.length; i++) {
            appendBits(u, uBitLength, a, i * pieceSize, n + 2);
            uBitLength += 3 * n + 5;
        }
        int numPiecesB = (b.length + pieceSize) / pieceSize;
        int[] v = new int[(numPiecesB * (3 * n + 5) + 31) / 32];
        int vBitLength = 0;
        for (int i = 0; i < numPiecesB && i * pieceSize < b.length; i++) {
            appendBits(v, vBitLength, b, i * pieceSize, n + 2);
            vBitLength += 3 * n + 5;
        }

        int[] gamma = multReverse(u, v, parallel);
        int[][] gammai = splitBits(gamma, 3 * n + 5);
        int halfNumPcs = numPieces / 2;

        int[][] zi = new int[gammai.length][];
        for (int i = 0; i < gammai.length; i++)
            zi[i] = gammai[i];
        for (int i = 0; i < gammai.length - halfNumPcs; i++)
            subModPow2(zi[i], gammai[i + halfNumPcs], n + 2);
        for (int i = 0; i < gammai.length - 2 * halfNumPcs; i++)
            addModPow2(zi[i], gammai[i + 2 * halfNumPcs], n + 2);
        for (int i = 0; i < gammai.length - 3 * halfNumPcs; i++)
            subModPow2(zi[i], gammai[i + 3 * halfNumPcs], n + 2);

        // zr mod Fn
        int[][] ai = splitInts(a, halfNumPcs, pieceSize, 1 << (n + 1 - 5));
        int[][] bi = splitInts(b, halfNumPcs, pieceSize, 1 << (n + 1 - 5));
        dft(ai, m, n);
        dft(bi, m, n);
        modFn(ai);
        modFn(bi);
        int[][] c = new int[halfNumPcs][];
        for (int i = 0; i < c.length; i++)
            c[i] = multModFn(ai[i], bi[i], parallel);
        idft(c, m, n);
        modFn(c);

        int[] z = new int[1 << (m + 1 - 5)];
        // calculate zr mod Fm from zr mod Fn and zr mod 2^(n+2), then add to z
        for (int i = 0; i < halfNumPcs; i++) {
            int[] eta = i >= zi.length ? new int[(n + 2 + 31) / 32] : zi[i];

            // zi = delta = (zi-c[i]) % 2^(n+2)
            subModPow2(eta, c[i], n + 2);

            // z += zr<<shift = [ci + delta*(2^2^n+1)] << [i*2^(n-1)]
            int shift = i * (1 << (n - 1 - 5));   // assume n>=6
            addShifted(z, c[i], shift);
            addShifted(z, eta, shift);
            addShifted(z, eta, shift + (1 << (n - 5)));
        }

        modFn(z);   // assume m>=5
        return z;
    }

    private static int[] reverse(int[] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[a.length - 1 - i];
        }
        return b;
    }

    private static boolean shouldMultiplySchoenhageStrassen(int bitLength) {
        return bitLength >= SCHOENHAGEN_STRASSEN_THRESHOLD;

        /*
        // The following values were determined experimentally on a 32-bit JVM.
        // SS is slower than Toom-Cook below ~247,000 bits (~74000 decimal digits)
        // and faster above ~1249000 bits (~376000 decimal digits).
        // Between those values, it changes several times.
        if (bitLength < 247000)
            return false;
        if (bitLength < 262144)   // 2^18
            return true;
        if (bitLength < 422000)
            return false;
        if (bitLength < 524288)   // 2^19
            return true;
        if (bitLength < 701000)
            return false;
        if (bitLength < 1048576)   // 2^20
            return true;
        if (bitLength < 1249000)
            return false;
        return true;

         */
    }

    /**
     * Divides an <code>int</code> array into pieces <code>bitLength</code> bits long.
     *
     * @param a
     * @param bitLength
     * @return a new array containing <code>bitLength</code> bits from <code>a</code> in each subarray
     */
    private static int[][] splitBits(int[] a, int bitLength) {
        int aIntIdx = 0;
        int aBitIdx = 0;
        int numPieces = (a.length * 32 + bitLength - 1) / bitLength;
        int pieceLength = (bitLength + 31) / 32;   // in ints
        int[][] b = new int[numPieces][pieceLength];
        for (int i = 0; i < b.length; i++) {
            int bitsRemaining = Math.min(bitLength, a.length * 32 - i * bitLength);
            int bIntIdx = 0;
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
                    aIntIdx++;
                }
                bBitIdx += bitsToCopy;
                if (bBitIdx >= 32) {
                    bBitIdx -= 32;
                    bIntIdx++;
                }
            }
        }
        return b;
    }

    /**
     * Splits an <code>int</code> array into pieces of <code>pieceSize ints</code> each, and
     * pads each piece to <code>targetPieceSize ints</code>.
     *
     * @param a               the input array
     * @param numPieces       the number of pieces to split the array into
     * @param pieceSize       the size of each piece in the input array in <code>ints</code>
     * @param targetPieceSize the size of each piece in the output array in <code>ints</code>
     * @return an array of length <code>numPieces</code> containing subarrays of length <code>targetPieceSize</code>
     */
    private static int[][] splitInts(int[] a, int numPieces, int pieceSize, int targetPieceSize) {
        int[][] ai = new int[numPieces][targetPieceSize];
        for (int i = 0; i < a.length / pieceSize; i++)
            System.arraycopy(a, i * pieceSize, ai[i], 0, pieceSize);
        System.arraycopy(a, a.length / pieceSize * pieceSize, ai[a.length / pieceSize], 0, a.length % pieceSize);
        return ai;
    }

    /**
     * Subtracts two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo 2^2^n+1.
     * The number n is <code>a.length*32/2</code>; in other words, n is half the number of bits in
     * <code>a</code>.<br/>
     * Both input values are given as <code>int</code> arrays; they must be the same length.
     * The result is returned in the first argument.
     *
     * @param a a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     * @param b a number in base 2^32 starting with the lowest digit; the length must be a power of 2
     */
    private static void subModFn(int[] a, int[] b, int pow2n) {
        addModFn(a, cyclicShiftLeftElements(b, pow2n / 32));
    }

    /**
     * Subtracts two <b>positive</b> numbers (meaning they are interpreted as unsigned) modulo 2^numBits.
     * Both input values are given as <code>int</code> arrays.
     * The result is returned in the first argument.
     *
     * @param a a number in base 2^32 starting with the lowest digit
     * @param b a number in base 2^32 starting with the lowest digit
     */
    private static void subModPow2(int[] a, int[] b, int numBits) {
        int numElements = (numBits + 31) / 32;
        int carry = 0;
        int i;
        for (i = 0; i < numElements; i++) {
            int diff = a[i] - b[i] - carry;
            // if
            carry = ((a[i] >>> 31) - (b[i] >>> 31) - (diff >>> 31)) >>> 31;
            a[i] = diff;
        }
        a[i - 1] &= -1 >>> (32 - (numBits % 32));
        for (; i < a.length; i++) {
            a[i] = 0;
        }
    }
}
