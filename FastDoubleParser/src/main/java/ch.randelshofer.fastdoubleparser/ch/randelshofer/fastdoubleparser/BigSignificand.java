package ch.randelshofer.fastdoubleparser;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * A mutable significand with a fixed number of bits.
 */
class BigSignificand {
    private final byte[] x;
    private int firstNonZeroWord;
    private static final long LONG_MASK = 0xffffffffL;
    private final int numInts;
    private final static VarHandle readIntBE =
            MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);

    public BigSignificand(long numBits) {
        if (numBits <= 0 || numBits >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("numBits=" + numBits);
        }
        int numLongs = (int) (numBits + 63) >>> 6;
        numInts = numLongs << 1;
        int numBytes = numInts << 2;
        x = new byte[numBytes];
        firstNonZeroWord = numInts;
    }

    /**
     * Multiplies the significand with the specified value in place.
     *
     * @param value the multiplication factor, must be a non-negative value
     * @throws ArrayIndexOutOfBoundsException on overflow
     */
    public void mul(int value) {
        long factor = value & LONG_MASK;
        long carry = 0;
        int i = numInts - 1;
        for (; i >= firstNonZeroWord; i--) {
            long product = factor * (x(i) & LONG_MASK) + carry;
            x(i, (int) product);
            carry = product >>> 32;
        }
        if (carry != 0) {
            x(i, (int) carry);
            firstNonZeroWord = i;
        }
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
        for (; i >= firstNonZeroWord; i--) {
            long product = factorL * (x(i) & LONG_MASK) + carry;
            x(i, (int) product);
            carry = product >>> 32;
        }
        if (carry != 0) {
            x(i, (int) carry);
            firstNonZeroWord = i;
        }
    }

    /**
     * Adds the specified value {@code addend * 10^addendExponent}
     * to this value.
     *
     * @param addend         the addend
     * @param addendExponent the exponent of the addend
     */
    public void add(int addend, int addendExponent) {
        throw new UnsupportedOperationException();
    }

    private int x(int i) {
        return (int) readIntBE.get(x, i << 2);
    }

    private void x(int i, int value) {
        readIntBE.set(x, i << 2, value);
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
        firstNonZeroWord = Math.min(firstNonZeroWord, i + 1);
    }

    @Override
    public String toString() {
        return toBigInteger().toString();
    }

    public BigInteger toBigInteger() {
        return new BigInteger(x);
    }

    public static long estimateNumBits(int numDecimalDigits) {
        // For the decimal number 10 we need log_2(10) = 3.3219 bits.
        // The following formula uses 3.322 * 1024 = 3401.8 rounded up
        // and adds 1, so that we overestimate but never underestimate
        // the number of bits.
        return (((numDecimalDigits * 3402L) >>> 10) + 1);
    }

}
