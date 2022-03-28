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

    private static final IntVector POWERS_OF_10 = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{1000_0000, 100_0000, 10_0000, 10000, 1000, 100, 10, 1}, 0);
    private static final IntVector POWERS_OF_16_SHIFTS = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{28, 24, 20, 16, 12, 8, 4, 0}, 0);
    public final static VarHandle readLongFromByteArray =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);

    static int tryToParseEightDigitsUtf8Swar(byte[] str, int offset) {
        long value = (long) readLongFromByteArray.get(str, offset);
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

    static int tryToParseEightDigitsUtf8Simd(byte[] a, int offset) {

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

    public static int tryToParseEightDigitsUtf16Simd(char[] a, int offset) {
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

    /**
     * Tries to parse eight digits at once.
     *
     * @param first  the first four characters
     * @param second the second four characters
     * @return the parsed digits or -1
     */
    public static long tryToParseEightDigitsUtf16Simd(long first, long second) {
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

    public static long tryToParseEightHexDigitsUtf16Simd(char[] a, int offset) {
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


    static long tryToParseEightHexDigitsUtf8Simd(byte[] a, int offset) {
        ByteVector vec = ByteVector.fromArray(ByteVector.SPECIES_64, a, offset)
                .sub((byte) '0');
        VectorMask<Byte> gt9Msk;
        // With an unsigned gt we only need to check for > 'f' - '0'
        if (vec.compare(UNSIGNED_GT, 'f' - '0').anyTrue()
                || (gt9Msk = vec.compare(UNSIGNED_GT, '9' - '0')).and(vec.compare(UNSIGNED_LT, 'a' - '0')).anyTrue()) {
            return -1L;
        }
        return vec
                .sub((byte) ('a' - '0' - 10), gt9Msk)
                .castShape(IntVector.SPECIES_256, 0)
                .lanewise(LSHL, POWERS_OF_16_SHIFTS)
                .reduceLanesToLong(ADD) & 0xffffffffL;
    }
}