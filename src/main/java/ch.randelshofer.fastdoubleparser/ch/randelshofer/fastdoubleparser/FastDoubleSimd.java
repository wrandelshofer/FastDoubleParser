/*
 * @(#)FastDoubleMath.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorMask;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

import static jdk.incubator.vector.VectorOperators.ADD;
import static jdk.incubator.vector.VectorOperators.LSHL;
import static jdk.incubator.vector.VectorOperators.UNSIGNED_GT;
import static jdk.incubator.vector.VectorOperators.UNSIGNED_LT;

/**
 * This class provides the mathematical functions needed by {@link FastDoubleParser}.
 * <p>
 * This is a C++ to Java port of Daniel Lemire's fast_double_parser.
 * <p>
 * The code contains enhancements from Daniel Lemire's fast_float_parser,
 * so that it can parse double Strings with very long sequences of numbers
 * <p>
 * References:
 * <dl>
 *     <dt>Leslie Lamport, Multiple Byte Processing with Full-Word Instructions</dt>
 *     <dd><a href="https://lamport.azurewebsites.net/pubs/multiple-byte.pdf">azurewebsites.net</a></dd>
 *
 *     <dt>Daniel Lemire, fast_double_parser, 4x faster than strtod.
 *     Apache License 2.0 or Boost Software License.</dt>
 *     <dd><a href="https://github.com/lemire/fast_double_parser">github.com</a></dd>
 *
 *     <dt>Daniel Lemire, fast_float number parsing library: 4x faster than strtod.
 *     Apache License 2.0.</dt>
 *     <dd><a href="https://github.com/fastfloat/fast_float">github.com</a></dd>
 *
 *     <dt>Daniel Lemire, Number Parsing at a Gigabyte per Second,
 *     Software: Practice and Experience 51 (8), 2021.
 *     arXiv.2101.11408v3 [cs.DS] 24 Feb 2021</dt>
 *     <dd><a href="https://arxiv.org/pdf/2101.11408.pdf">arxiv.org</a></dd>
 * </dl>
 * </p>
 */
class FastDoubleSimd {

    public final static VarHandle readLongFromByteArrayLittleEndian =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    public final static VarHandle readLongFromByteArrayBigEndian =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    private static final IntVector POWERS_OF_10 = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{1000_0000, 100_0000, 10_0000, 10000, 1000, 100, 10, 1}, 0);
    private static final IntVector POWERS_OF_16_SHIFTS = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{28, 24, 20, 16, 12, 8, 4, 0}, 0);

    public static int tryToParseEightDigitsUtf16Vector(char[] a, int offset) {
        ShortVector vec = ShortVector.fromCharArray(ShortVector.SPECIES_128, a, offset)
                .sub((short) '0');
        // With an unsigned gt we only need to check for > 9
        if (vec.compare(UNSIGNED_GT, 9).anyTrue()) {
            return -1;
        }
        return (int) vec
                .castShape(IntVector.SPECIES_256, 0)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);
    }

    /**
     * Tries to parse eight digits at once.
     *
     * @param first  the first four characters
     * @param second the second four characters
     * @return the parsed digits or -1
     */
    public static long tryToParseEightDigitsUtf16Vector(long first, long second) {
        ShortVector vec = LongVector.zero(LongVector.SPECIES_128)
                .withLane(0, first)
                .withLane(1, second)
                .reinterpretAsShorts()
                .sub((short) '0');

        // With an unsigned gt we only need to check for > 9
        if (vec.compare(UNSIGNED_GT, 9).anyTrue()) {
            return -1L;
        }
        return vec
                .castShape(IntVector.SPECIES_256, 0)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);
    }

    /**
     * Tries to parse eight digits at once.
     *
     * @param first  the first four characters
     * @param second the second four characters
     * @return the parsed digits or -1
     */
    public static long tryToParseEightDigitsUtf16Swar(long first, long second) {
        long fval = first - 0x0030_0030_0030_0030L;
        long sval = second - 0x0030_0030_0030_0030L;

        long fdet = ((first + 0x0046_0046_0046_0046L) | fval);
        long sdet = ((second + 0x0046_0046_0046_0046L) | sval);
        if (((fdet | sdet) & 0xff80_ff80_ff80_ff80L) != 0L) {
            return -1;
        }

        fval = (fval * 0xa_00_01L) >>> 16;// (10<<32)+1
        sval = (sval * 0xa_00_01L) >>> 16;// (10<<32)+1

        fval = 100 * (fval & 0xff) + (fval >>> 32);
        sval = 100 * (sval & 0xff) + (sval >>> 32);

        return sval + 10000 * fval;
    }

    static int tryToParseEightDigitsUtf8Vector(byte[] a, int offset) {
        ByteVector vec = ByteVector.fromArray(ByteVector.SPECIES_64, a, offset)
                .sub((byte) '0');
        // With an unsigned gt we only need to check for > 9
        if (vec.compare(UNSIGNED_GT, 9).anyTrue()) {
            return -1;
        }
        return (int) vec
                .castShape(IntVector.SPECIES_256, 0)
                .mul(POWERS_OF_10)
                .reduceLanesToLong(ADD);
    }

    static int tryToParseEightDigitsUtf8Swar(byte[] str, int offset) {
        long value = (long) readLongFromByteArrayLittleEndian.get(str, offset);
        return tryToParseEightDigitsUtf8Swar(value);
    }

    /**
     * Tries to parse eight digits from a long using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param value contains 8 ascii characters in little endian order
     * @return the parsed number,
     * returns -1 if {@code value} does not contain 8 ascii digits
     */
    public static int tryToParseEightDigitsUtf8Swar(long value) {
        long val = value - 0x3030303030303030L;
        long det = ((value + 0x4646464646464646L) | val) &
                0x8080808080808080L;
        if (det != 0L) {
            return -1;
        }

        // The last 2 multiplications in this algorithm are independent of each
        // other.
        long mask = 0x000000FF_000000FFL;
        val = (val * 0xa_01L) >>> 8;// 1+(10<<8)
        val = (((val & mask) * 0x000F4240_00000064L)//100 + (1000000 << 32)
                + (((val >>> 16) & mask) * 0x00002710_00000001L)) >>> 32;// 1 + (10000 << 32)
        return (int) val;
    }

    public static long tryToParseEightHexDigitsUtf16Vector(char[] a, int offset) {
        ShortVector vec = ShortVector.fromCharArray(ShortVector.SPECIES_128, a, offset)
                .sub((short) '0');
        VectorMask<Short> gt9Msk;
        // With an unsigned gt we only need to check for > 'f' - '0'
        if (vec.compare(UNSIGNED_GT, 'f' - '0').anyTrue()
                || (gt9Msk = vec.compare(UNSIGNED_GT, '9' - '0')).and(vec.compare(UNSIGNED_LT, 'a' - '0')).anyTrue()) {
            return -1L;
        }
        return vec
                .sub((short) ('a' - '0' - 10), gt9Msk)
                .castShape(IntVector.SPECIES_256, 0)
                .lanewise(LSHL, POWERS_OF_16_SHIFTS)
                .reduceLanesToLong(ADD) & 0xffffffffL;
    }


    /**
     * Tries to parse eight hex digits from a byte array using the
     * Java vector API.
     *
     * @param a      contains 8 ascii characters
     * @param offset the offset of the first character in {@code a}
     *               returns -1 if {@code value} does not contain 8 ascii digits
     */
    static long tryToParseEightHexDigitsUtf8Vector(byte[] a, int offset) {
        ByteVector vec = ByteVector.fromArray(ByteVector.SPECIES_64, a, offset)
                .sub((byte) '0');
        VectorMask<Byte> gt9Msk;
        // With an unsigned gt we only need to check for > 'f' - '0'
        if (vec.compare(UNSIGNED_GT, 'f' - '0').anyTrue()
                || (gt9Msk = vec.compare(UNSIGNED_GT, '9' - '0'))
                .and(vec.compare(UNSIGNED_LT, 'a' - '0')).anyTrue()) {
            return -1L;
        }
        return vec
                .sub((byte) ('a' - '0' - 10), gt9Msk)
                .castShape(IntVector.SPECIES_256, 0)
                .lanewise(LSHL, POWERS_OF_16_SHIFTS)
                .reduceLanesToLong(ADD) & 0xffffffffL;
    }

    /**
     * Tries to parse eight hex digits from a byte array using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param a      contains 8 ascii characters
     * @param offset the offset of the first character in {@code a}
     *               returns -1 if {@code value} does not contain 8 ascii digits
     */
    static long tryToParseEightHexDigitsUtf8Swar(byte[] a, int offset) {
        return tryToParseEightHexDigitsUtf8Swar((long) readLongFromByteArrayBigEndian.get(a, offset));
    }

    /**
     * Tries to parse eight digits from a long using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param value contains 8 ascii characters in big endian order
     * @return the parsed number,
     * returns -1 if {@code value} does not contain 8 ascii digits
     */
    static long tryToParseEightHexDigitsUtf8Swar(long value) {
        // The following code is based on the technique presented in the paper
        // by Leslie Lamport.


        // Subtract character '0' (0x30) from each of the eight characters
        long vec = value - 0x3030303030303030L;

        // Create a predicate for all bytes which are greater than '9'-'0' (0x09).
        // The predicate is true if the hsb of a byte is set: (predicate & 0xa0) != 0.
        long gt_09 = vec + (0x09090909_09090909L ^ 0x7f7f7f7f_7f7f7f7fL);
        gt_09 = gt_09 & 0x80808080_80808080L;
        // Create a predicate for all bytes which are greater or equal 'a'-'0' (0x30).
        // The predicate is true if the hsb of a byte is set.
        long ge_30 = vec + (0x30303030_30303030L ^ 0x7f7f7f7f_7f7f7f7fL);
        ge_30 = ge_30 & 0x80808080_80808080L;

        // Create a predicate for all bytes which are smaller equal than 'f'-'0' (0x37).
        long le_37 = 0x37373737_37373737L + (vec ^ 0x7f7f7f7f_7f7f7f7fL);
        le_37 = le_37 & 0x80808080_80808080L;


        // If a character is greater than '9' then it must be greater equal 'a'
        // and smaller  'f'.
        if (gt_09 != (ge_30 & le_37)) {
            return -1;
        }

        // Expand the predicate to a byte mask
        long gt_09mask = ((gt_09 & 0x80808080_80808080L) >>> 7) * 0xffL;

        // Subtract 'a'-'0'+10 (0x27) from all bytes that are greater than 0x09.
        long v = (vec & ~gt_09mask) | (vec - (0x27272727_27272727L & gt_09mask)) & gt_09mask;

        // Now compact all lower nibbles
        long v2 = v | v >>> 4;
        long v3 = v2 & 0x00ff00ff_00ff00ffL;
        long v4 = v3 | v3 >>> 8;
        long v5 = ((v4 >>> 16) & 0xffff_0000L) | v4 & 0xffffL;

        return v5;
    }
}