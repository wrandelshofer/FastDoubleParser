/*
 * @(#)BigSignificand.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * A mutable non-negative significand with a fixed number of bits.
 */
class BigSignificand {
    private static final long LONG_MASK = 0xffffffffL;
    private final static VarHandle readIntBE =
            MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private final int numInts;
    private final byte[] x;
    private int firstNonZeroInt;

    /**
     * Creates a new instance with the specified number in bits.
     *
     * @param numBits the number of bits in range {@literal [0, Integer.MAX_VALUE)}.
     */
    public BigSignificand(long numBits) {
        if (numBits <= 0 || numBits >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("numBits=" + numBits);
        }
        int numLongs = (int) ((numBits + 63) >>> 6) + 1;
        numInts = numLongs << 1;
        int numBytes = numLongs << 3;
        x = new byte[numBytes];
        firstNonZeroInt = numInts;
    }

    /**
     * Adds the specified value to the significand in place.
     *
     * @param value the addend, must be a non-negative value
     * @throws ArrayIndexOutOfBoundsException on overflow
     */
    public void add(int value) {
        if (value == 0) {
            return;
        }
        long carry = value & LONG_MASK;
        int i = numInts - 1;
        for (; carry != 0; i--) {
            long sum = (x(i) & LONG_MASK) + carry;
            x(i, (int) sum);
            carry = sum >>> 32;
        }
        firstNonZeroInt = Math.min(firstNonZeroInt, i + 1);
    }

    /**
     * Multiplies the significand with the specified factor in place,
     * and then adds the specified addend to it (also in place).
     *
     * @param factor the multiplication factor, must be a non-negative value
     * @param addend the addend, must be a non-negative value
     * @throws ArrayIndexOutOfBoundsException on overflow
     */
    public void fma(int factor, int addend) {
        long factorL = factor & LONG_MASK;
        long carry = addend;
        int i = numInts - 1;
        for (; i >= firstNonZeroInt; i--) {
            long product = factorL * (x(i) & LONG_MASK) + carry;
            x(i, (int) product);
            carry = product >>> 32;
        }
        if (carry != 0) {
            x(i, (int) carry);
            firstNonZeroInt = i;
        }
    }

    /**
     * Converts the BigSignificand to a BigInteger.
     * @return a new BigInteger instance
     */
    public BigInteger toBigInteger() {
        return new BigInteger(x);
    }

    private void x(int i, int value) {
        readIntBE.set(x, i << 2, value);
    }

    private int x(int i) {
        return (int) readIntBE.get(x, i << 2);
    }
}
