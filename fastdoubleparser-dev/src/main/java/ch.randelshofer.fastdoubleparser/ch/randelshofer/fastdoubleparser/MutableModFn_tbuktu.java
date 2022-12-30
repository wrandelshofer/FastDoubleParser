/*
 * @(#)MutableModFn.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Arrays;

class MutableModFn_tbuktu {
    long[] digits;

    /**
     * Creates a {@code MutableModFn} number from a {@code long} array whose length
     * must be 2<sup>n-6</sup>+1 for some n. The first element must be 0 or 1.
     * The caller is trusted to pass in a valid array.<br/>
     * No copy of the array is made; its contents will reflect operations on the
     * {@code MutableModFn} object.
     *
     * @param digits an long array in the same format as {@code BigInteger#mag}
     */
    MutableModFn_tbuktu(long[] digits) {
        this.digits = digits;
    }

    /**
     * Creates a zero value. {@code length} must be 2<sup>n-6</sup>+1 for some n.
     *
     * @param length
     */
    MutableModFn_tbuktu(int length) {
        digits = new long[length];
    }

    /**
     * Copies this {@code MutableModFn}'s value into another {@code MutableModFn}.
     *
     * @param b
     */
    void copyTo(MutableModFn_tbuktu b) {
        System.arraycopy(digits, 0, b.digits, 0, digits.length);
    }

    /**
     * Adds another {@code MutableModFn} to this number.
     *
     * @param b
     */
    void add(MutableModFn_tbuktu b) {
        long carry = 0;
        for (int i = digits.length - 1; i >= 0; i--) {
            long sum = digits[i] + b.digits[i] + carry;
            // if signBit(sum) < signBit(digits[i])+signBit(addend[i]) then carry = 1; else carry=0
            carry = ((sum >>> 63) - (digits[i] >>> 63) - (b.digits[i] >>> 63)) >>> 63;
            digits[i] = sum;
        }

        // take a mod Fn by adding any remaining carry bit to the lowest bit;
        // since Fn is congruent to 1 (mod 2^n), it suffices to add 1
        int i = digits.length - 1;
        boolean carryFlag = carry != 0;
        while (carryFlag && i >= 0) {
            long sum = digits[i] + 1;
            digits[i] = sum;
            carryFlag = sum == 0;
            i--;
        }

        reduce();
    }

    /**
     * Subtracts another {@code MutableModFn} from this number.
     *
     * @param b
     */
    void subtract(MutableModFn_tbuktu b) {
        boolean borrow = false;
        for (int i = digits.length - 1; i >= 0; i--) {
            long diff = digits[i] - b.digits[i];
            if (borrow) {
                diff--;
            }
            borrow = ((diff >>> 63) > (digits[i] >>> 63) - (b.digits[i] >>> 63));   // borrow if signBit(diff) > signBit(digits[i])-signBit(b.digits[i])
            digits[i] = diff;
        }

        // if we borrowed from the most significant long, subtract 2^2^n which is the same as adding 1 (mod Fn)
        if (borrow) {
            digits[0]++;   // undo borrow
            int i = digits.length - 1;
            boolean carry = true;
            while (carry && i >= 0) {
                long sum = digits[i] + 1;
                digits[i] = sum;
                carry = sum == 0;
                i--;
            }
        }
    }

    /**
     * Multiplies this number by another {@code MutableModFn}.
     *
     * @param b
     * @param parallel
     */
    void multiply(MutableModFn_tbuktu b, boolean parallel) {
        // if a=b=2^n, a*b=1 (mod Fn)
        if (digits[0] == 1 && b.digits[0] == 1) {
            Arrays.fill(digits, 0);
            digits[digits.length - 1] = 1;
        }
        // otherwise, a*b will fit into 2*2^n bits
        else {
            int[] intDigits = toIntArrayOdd(digits);
            int[] intBDigits = toIntArrayOdd(b.digits);
            int[] cInt = SchoenhageStrassenMultiplier_tbuktu.multiply(intDigits, intBDigits, parallel);
            // zero-pad c to make it 2*2^n in length, and convert it to long[]
            int[] cIntPad = new int[intDigits.length - 1 + intBDigits.length - 1];
            System.arraycopy(cInt, 0, cIntPad, cIntPad.length - cInt.length, cInt.length);
            long[] c = toLongArrayEven(cIntPad);
            // reduce c mod Fn which makes the first c.length/2-1 longs zero; return the others
            reduceWide(c);
            System.arraycopy(c, c.length / 2 - 1, digits, 0, c.length / 2 + 1);
        }
    }

    void multiplyOld(MutableModFn_tbuktu b, boolean parallel) {
        // if a=b=2^n, a*b=1 (mod Fn)
        if (digits[0] == 1 && b.digits[0] == 1) {
            Arrays.fill(digits, 0);
            digits[digits.length - 1] = 1;
        }
        // otherwise, a*b will fit into 2*2^n bits
        else {
            int[] intDigits = toIntArrayOdd(digits);
            BigInteger aBigInt = FastIntegerMath.newBigInteger(1, intDigits);
            int[] intBDigits = toIntArrayOdd(b.digits);
            BigInteger bBigInt = FastIntegerMath.newBigInteger(1, intBDigits);
            int[] cInt = FastIntegerMath.getMagnitude(SchoenhageStrassenMultiplier_tbuktu.multiply(aBigInt, bBigInt, parallel));
            // zero-pad c to make it 2*2^n in length, and convert it to long[]
            int[] cIntPad = new int[intDigits.length - 1 + intBDigits.length - 1];
            System.arraycopy(cInt, 0, cIntPad, cIntPad.length - cInt.length, cInt.length);
            long[] c = toLongArrayEven(cIntPad);
            // reduce c mod Fn which makes the first c.length/2-1 longs zero; return the others
            reduceWide(c);
            System.arraycopy(c, c.length / 2 - 1, digits, 0, c.length / 2 + 1);
        }
    }

    /**
     * Squares this number.
     *
     * @see #multiply(MutableModFn_tbuktu, boolean)
     */
    void square(boolean parallel) {
        // if a=2^n, a^2=1 (mod Fn)
        if (digits[0] == 1) {
            Arrays.fill(digits, 0);
            digits[digits.length - 1] = 1;
        }
        // otherwise, a^2 will fit into 2*2^n bits
        else {
            int[] intDigits = toIntArrayOdd(digits);
            BigInteger aBigInt = FastIntegerMath.newBigInteger(1, intDigits);
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
            int[] cInt = FastIntegerMath.getMagnitude(SchoenhageStrassenMultiplier_tbuktu.square(aBigInt, parallel));
            // zero-pad cInt to make it 2*2^n bits in length, and convert it to long[]
            int[] cIntPad = new int[2 * intDigits.length - 2];
            System.arraycopy(cInt, 0, cIntPad, cIntPad.length - cInt.length, cInt.length);
            long[] c = toLongArrayEven(cIntPad);
            // reduce c mod Fn which makes the first c.length/2-1 longs zero; return the others
            reduceWide(c);
            System.arraycopy(c, c.length / 2 - 1, digits, 0, c.length / 2 + 1);
        }
    }

    /**
     * Reduces this number modulo F<sub>n</sub>.<br/>
     * {@code digits[0]} will be 0 or 1.
     */
    private void reduce() {
        // Reduction modulo Fn is done by subtracting the most significant long from the least significant long
        int len = digits.length;
        long bi = digits[0];
        long diff = digits[len - 1] - bi;
        boolean borrow = ((diff >>> 63) > (digits[len - 1] >>> 63) - (bi >>> 63));   // borrow if signBit(diff) > signBit(digits[len-1])-signBit(digits[0])
        digits[len - 1] = diff;
        digits[0] = 0;   // because we subtracted digits[0] from digits[len-1]
        if (borrow) {
            int i = len - 2;
            do {
                diff = digits[i] - 1;
                digits[i] = diff;
                borrow = diff == -1;
                i--;
            } while (borrow && i >= 0);
        }

        // if we borrowed from the most significant long, subtract 2^2^n which is the same as adding 1 (mod Fn)
        if (borrow) {
            int i = digits.length - 1;
            boolean carry = true;
            digits[0] = 0;   // increment digits[0] by 1 to make it 0
            while (carry && i >= 0) {
                long sum = digits[i] + 1;
                digits[i] = sum;
                carry = sum == 0;
                i--;
            }
        }
    }

    /**
     * Like {@link #reduce()} but works on an array of length 2^(n+1).
     */
    private static void reduceWide(long[] a) {
        // Reduction modulo Fn is done by subtracting the upper half from the lower half
        int len = a.length;
        boolean carry = false;
        for (int i = len - 1; i >= len / 2; i--) {
            long bi = a[i - len / 2];
            long diff = a[i] - bi;
            if (carry) {
                diff--;
            }
            carry = ((diff >>> 63) > (a[i] >>> 63) - (bi >>> 63));   // carry if signBit(diff) > signBit(a)-signBit(b)
            a[i] = diff;
        }
        for (int i = len / 2 - 1; i >= 0; i--)
            a[i] = 0;
        // if result is negative, add Fn; since Fn is congruent to 1 (mod 2^n), it suffices to add 1
        if (carry) {
            int j = len - 1;
            do {
                long sum = a[j] + 1;
                a[j] = sum;
                carry = sum == 0;
                j--;
                if (j <= 0) {
                    break;
                }
            } while (carry);
        }
    }

    /**
     * Like {@link #reduceWide(long[])} but works on an int array.
     */
    static void reduce(int[] digits) {
        // Reduction modulo Fn is done by subtracting the most significant int from the least significant int
        int len = digits.length;
        int bi = digits[0];
        int diff = digits[len - 1] - bi;
        boolean borrow = ((diff >>> 31) > (digits[len - 1] >>> 31) - (bi >>> 31));   // borrow if signBit(diff) > signBit(digits[len-1])-signBit(digits[0])
        digits[len - 1] = diff;
        digits[0] = 0;   // because we subtracted digits[0] from digits[len-1]
        if (borrow) {
            int i = len - 2;
            do {
                diff = digits[i] - 1;
                digits[i] = diff;
                borrow = diff == -1;
                i--;
            } while (borrow && i >= 0);
        }

        // if we borrowed from the most significant int, subtract 2^2^n which is the same as adding 1 (mod Fn)
        if (borrow) {
            int i = digits.length - 1;
            boolean carry = true;
            digits[0] = 0;   // increment digits[0] by 1 to make it 0
            while (carry && i >= 0) {
                int sum = digits[i] + 1;
                digits[i] = sum;
                carry = sum == 0;
                i--;
            }
        }
    }

    /**
     * Multiplies this number by 2<sup>-shiftAmtBits</sup> modulo 2<sup>2<sup>n</sup></sup>+1 where 2<sup>n</sup>=
     * <code>(digits.length-1)*64</code>.<br/>
     * "Right" means towards the higher array indices and the lower bits<br/>.
     * This is equivalent to extending the number to <code>2*(digits.length-1)</code> longs and cyclicly
     * shifting to the right by <code>shiftAmt</code> bits.<br/>
     * The result is placed in the second argument.
     *
     * @param shiftAmtBits the shift amount in bits; must be less than <code>64*2*(digits.length-1))</code>
     * @param b            the return value; must have room for at least as many digits as <code>this</code>
     */
    void shiftRight(int shiftAmtBits, MutableModFn_tbuktu b) {
        int len = digits.length;
        if (shiftAmtBits > 64 * (len - 1)) {
            shiftLeft(64 * 2 * (len - 1) - shiftAmtBits, b);
            return;
        }

        int shiftAmtLongs = shiftAmtBits / 64;   // number of longs to shift
        if (shiftAmtLongs > 0) {
            boolean borrow = false;

            // shift the digits that stay positive, except a[len-1] which is special
            for (int i = 1; i < len - shiftAmtLongs; i++) {
                long diff = digits[i];
                if (borrow) {
                    diff--;
                }
                b.digits[shiftAmtLongs + i] = diff;
                borrow = diff == -1 && borrow;
            }

            // subtract a[len-1] from a[0]
            long diff = digits[0] - digits[len - 1];
            if (borrow) {
                diff--;
                borrow = diff == -1;
            } else {
                borrow = digits[0] == 0 && digits[len - 1] != 0;   // a[0] can only be 0 or 1; if digits[0]!=0, digits[len-1]==0
            }
            b.digits[shiftAmtLongs] = diff;

            // using the fact that adding x*(Fn-1) is the same as subtracting x,
            // subtract digits shifted off the right, except for a[0] which is special
            for (int i = 1; i < shiftAmtLongs; i++) {
                b.digits[shiftAmtLongs - i] = -digits[len - 1 - i];
                if (borrow) {
                    b.digits[shiftAmtLongs - i]--;
                }
                borrow = b.digits[shiftAmtLongs - i] != 0 || borrow;
            }

            // if we borrowed from the most significant long, add 1 to the overall number
            boolean carry = borrow;
            if (carry) {
                // increment b[0] and decrement b[len-1]
                b.digits[0] = 0;
                int i = len - 1;
                do {
                    long sum = b.digits[i] + 1;
                    b.digits[i] = sum;
                    carry = sum == 0;
                    i--;
                } while (carry && i >= 0);
            } else {
                b.digits[0] = 0;
            }
        } else {
            System.arraycopy(digits, 0, b.digits, 0, len);
        }

        int shiftAmtFrac = shiftAmtBits % 64;
        if (shiftAmtFrac != 0) {
            long bhi = b.digits[len - 1] << (64 - shiftAmtFrac);

            // do remaining digits
            b.digits[len - 1] >>>= shiftAmtFrac;
            for (int i = len - 1; i > 0; i--) {
                b.digits[i] |= b.digits[i - 1] << (64 - shiftAmtFrac);
                b.digits[i - 1] >>>= shiftAmtFrac;
            }

            // b[len-1] spills over into b[1]
            long diff = b.digits[1] - bhi;
            boolean borrow = ((diff >>> 63) > (b.digits[1] >>> 63) - (bhi >>> 63));   // borrow if signBit(diff) > signBit(a)-signBit(b)
            b.digits[1] = diff;

            // if we borrowed from b[0], add 1 to the overall number
            boolean carry = borrow;
            if (carry) {
                // increment b[0] and decrement b[len-1]
                b.digits[0] = 0;
                int i = len - 1;
                do {
                    long sum = b.digits[i] + 1;
                    b.digits[i] = sum;
                    carry = sum == 0;
                    i--;
                } while (carry && i >= 0);
            } else {
                b.digits[0] = 0;
            }
        }
    }

    /**
     * Multiplies this number by 2<sup>shiftAmt</sup> modulo 2<sup>2<sup>n</sup></sup>+1 where 2<sup>n</sup>=
     * <code>(digits.length-1)*64</code>.<br/>
     * "Left" means towards the higher array indices and the lower bits<br/>.
     * This is equivalent to extending the number to <code>2*(digits.length-1)</code> longs and cyclicly
     * shifting to the left by <code>shiftAmt</code> bits.<br/>
     * The result is placed in the second argument.
     *
     * @param shiftAmtBits the shift amount in bits; must be less than <code>64*2*(digits.length-1))</code>
     * @param b            the return value; must have room for at least as many digits as <code>this</code>
     */
    void shiftLeft(int shiftAmtBits, MutableModFn_tbuktu b) {
        int len = digits.length;

        if (shiftAmtBits > 64 * (len - 1)) {
            shiftRight(64 * 2 * (len - 1) - shiftAmtBits, b);
            return;
        }

        int shiftAmtLongs = shiftAmtBits / 64;   // number of longs to shift
        if (shiftAmtLongs > 0) {
            boolean borrow = false;
            // using the fact that adding x*(Fn-1) is the same as subtracting x,
            // subtract digits shifted outside the [0..Fn-2] range, except for digits[0] which is special
            for (int i = 0; i < shiftAmtLongs; i++) {
                b.digits[len - 1 - i] = -digits[shiftAmtLongs - i];
                if (borrow) {
                    b.digits[len - 1 - i]--;
                }
                borrow = b.digits[len - 1 - i] != 0 || borrow;
            }

            // subtract digits[0] from digits[len-1] (they overlap unless numElements=len-1)
            long diff;
            if (shiftAmtLongs < len - 1) {
                diff = digits[len - 1] - digits[0];
            } else   // no overlap
            {
                diff = -digits[0];
            }
            if (borrow) {
                diff--;
                borrow = diff == -1;
            } else {
                borrow = digits[0] == 1 && diff == -1;   // digits[0] can only be 0 or 1
            }
            b.digits[len - 1 - shiftAmtLongs] = diff;

            // finally, shift the digits that stay in the [0..Fn-2] range
            for (int i = 1; i < len - shiftAmtLongs - 1; i++) {
                diff = digits[len - 1 - i];
                if (borrow) {
                    diff--;
                }
                b.digits[len - 1 - shiftAmtLongs - i] = diff;
                borrow = diff == -1 && borrow;
            }

            // if we borrowed from the most significant long, add 1 to the overall number
            boolean carry = borrow;
            if (carry) {
                // increment b[0] and decrement b[len-1]
                b.digits[0] = 0;
                int i = len - 1;
                do {
                    long sum = b.digits[i] + 1;
                    b.digits[i] = sum;
                    carry = sum == 0;
                    i--;
                } while (carry && i >= 0);
            } else {
                b.digits[0] = 0;
            }
        } else {
            System.arraycopy(digits, 0, b.digits, 0, len);
        }

        int shiftAmtFrac = shiftAmtBits % 64;
        if (shiftAmtFrac != 0) {
            b.digits[0] <<= shiftAmtFrac;   // no spill-over because 0<=digits[0]<=1 and shiftAmtFrac<=63
            for (int i = 1; i < len; i++) {
                b.digits[i - 1] |= b.digits[i] >>> (64 - shiftAmtFrac);
                b.digits[i] <<= shiftAmtFrac;
            }
        }

        b.reduce();
    }

    /**
     * digits.length must be an even number
     */
    private static long[] toLongArrayEven(int[] digits) {
        long[] longDigits = new long[digits.length / 2];
        for (int i = 0; i < longDigits.length; i++)
            longDigits[i] = (((long) digits[2 * i]) << 32) | (digits[2 * i + 1] & 0xFFFFFFFFL);
        return longDigits;
    }

    /**
     * digits.length must be an odd number
     */
    static int[] toIntArrayOdd(long[] digits) {
        int[] intDigits = new int[digits.length * 2 - 1];
        intDigits[0] = (int) digits[0];
        for (int i = 1; i < digits.length; i++) {
            intDigits[2 * i - 1] = (int) (digits[i] >>> 32);
            intDigits[2 * i] = (int) (digits[i] & -1);
        }
        return intDigits;
    }
}