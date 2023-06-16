/*
 * @(#)FastDoubleVector.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import jdk.incubator.vector.*;

import static jdk.incubator.vector.VectorOperators.*;

/**
 * This class provides methods for parsing multiple characters at once using
 * vector instructions.
 * <p>
 * References:
 * <dl>
 *     <dt>Leslie Lamport, Multiple Byte Processing with Full-Word Instructions</dt>
 *     <dd><a href="https://lamport.azurewebsites.net/pubs/multiple-byte.pdf">azurewebsites.net</a></dd>
 *
 *     <dt>Daniel Lemire, fast_float number parsing library: 4x faster than strtod.
 *     <a href="https://github.com/fastfloat/fast_float/blob/dc88f6f882ac7eb8ec3765f633835cb76afa0ac2/LICENSE-MIT">MIT License</a>.</dt>
 *     <dd><a href="https://github.com/fastfloat/fast_float">github.com</a></dd>
 *
 *     <dt>Daniel Lemire, Number Parsing at a Gigabyte per Second,
 *     Software: Practice and Experience 51 (8), 2021.
 *     arXiv.2101.11408v3 [cs.DS] 24 Feb 2021</dt>
 *     <dd><a href="https://arxiv.org/pdf/2101.11408.pdf">arxiv.org</a></dd>
 * </dl>
 * </p>
 */
class FastDoubleVector {
    private static final IntVector POWERS_OF_10 = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{1000_0000, 100_0000, 10_0000, 10000, 1000, 100, 10, 1}, 0);
    private static final IntVector POWERS_OF_16_SHIFTS_BE = IntVector.fromArray(IntVector.SPECIES_256,
            new int[]{28, 24, 20, 16, 12, 8, 4, 0}, 0);
    private static final LongVector POWERS_OF_16_SHIFTS_LE = LongVector.fromArray(LongVector.SPECIES_512,
            new long[]{16, 20, 24, 28, 0, 4, 8, 12}, 0);

    /**
     * Tries to parse eight digits at once using the
     * Java Vector API.
     *
     * @param str    a character sequence
     * @param offset the index of the first character in the character sequence
     * @return the parsed digits or -1
     */
    public static int tryToParseEightDigits(CharSequence str, int offset) {
        // Performance: We extract the chars in two steps so that we
        //              can benefit from out of order execution in the CPU.
        long first = str.charAt(offset)
                | (long) str.charAt(offset + 1) << 16
                | (long) str.charAt(offset + 2) << 32
                | (long) str.charAt(offset + 3) << 48;

        long second = str.charAt(offset + 4)
                | (long) str.charAt(offset + 5) << 16
                | (long) str.charAt(offset + 6) << 32
                | (long) str.charAt(offset + 7) << 48;

        return FastDoubleVector.tryToParseEightDigitsUtf16(first, second);
    }

    /**
     * Tries to parse eight decimal digits from a char array using the
     * Java Vector API.
     *
     * @param a      contains 8 utf-16 characters starting at offset
     * @param offset the offset into the array
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 hex digits
     */
    public static int tryToParseEightDigitsUtf16(char[] a, int offset) {
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
     * Tries to parse eight digits at once using the
     * Java Vector API.
     *
     * <pre>{@literal
     * char[] chars = ...;
     * long first  = chars[0]|(chars[1]<<16)|(chars[2]<<32)|(chars[3]<<48);
     * long second = chars[4]|(chars[5]<<16)|(chars[6]<<32)|(chars[7]<<48);
     * }</pre>
     *
     * @param first  the first four characters
     * @param second the second four characters
     * @return the parsed digits or -1
     */
    public static int tryToParseEightDigitsUtf16(long first, long second) {
        ShortVector vec = LongVector.zero(LongVector.SPECIES_128)
                .withLane(0, first)
                .withLane(1, second)
                .reinterpretAsShorts()
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
     * Tries to parse eight decimal digits from a byte array using the
     * Java Vector API.
     *
     * @param a      contains 8 ascii characters
     * @param offset the offset of the first character in {@code a}
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 digits
     */
    public static int tryToParseEightDigitsUtf8(byte[] a, int offset) {
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

    /**
     * @param first  the first 4 characters in little endian order
     * @param second the second 4 characters in little endian order
     * @return the parsed value or -1
     */
    public static long tryToParseEightHexDigitsUtf16(long first, long second) {
        ShortVector c = LongVector.zero(LongVector.SPECIES_128)
                .withLane(0, first)
                .withLane(1, second)
                .reinterpretAsShorts();
        ShortVector lowerCase = c.or((short) 0x20);
        VectorMask<Short> ge_a = lowerCase.compare(UNSIGNED_GE, 'a');
        if (!c.compare(UNSIGNED_GE, '0').and(c.compare(UNSIGNED_LE, '9'))
                .or(ge_a.and(lowerCase.compare(UNSIGNED_LE, 'f')))
                .allTrue()) {
            return -1;
        }
        return lowerCase.sub((byte) '0')
                .sub((byte) ('a' - '0' - 10), ge_a)
                .castShape(IntVector.SPECIES_256, 0)
                .lanewise(LSHL, POWERS_OF_16_SHIFTS_BE)
                .reduceLanesToLong(ADD) & 0xffffffffL;
    }

    /**
     * Tries to parse eight hex digits from a char array using the
     * Java Vector API.
     *
     * @param a      contains 8 utf-16 characters starting at offset
     * @param offset the offset into the array
     * @return the parsed number,
     * returns a negative value if {@code a} does not contain 8 hex digits
     */
    public static long tryToParseEightHexDigitsUtf16(char[] a, int offset) {
        ShortVector c = ShortVector.fromCharArray(ShortVector.SPECIES_128, a, offset);
        ShortVector lowerCase = c.or((short) 0x20);
        VectorMask<Short> ge_a = lowerCase.compare(UNSIGNED_GE, 'a');
        if (!c.compare(UNSIGNED_GE, '0').and(c.compare(UNSIGNED_LE, '9'))
                .or(ge_a.and(lowerCase.compare(UNSIGNED_LE, 'f')))
                .allTrue()) {
            return -1;
        }
        return lowerCase.sub((byte) '0')
                .sub((byte) ('a' - '0' - 10), ge_a)
                .castShape(IntVector.SPECIES_256, 0)
                .lanewise(LSHL, POWERS_OF_16_SHIFTS_BE)
                .reduceLanesToLong(ADD) & 0xffffffffL;
    }

    /**
     * Tries to parse eight hex digits from a byte array using the
     * Java vector API.
     *
     * @param a      contains 8 ascii characters
     * @param offset the offset of the first character in {@code a}
     *               returns a negative value if {@code value} does not contain 8 digits
     */
    public static long tryToParseEightHexDigitsUtf8(byte[] a, int offset) {
        ByteVector c = ByteVector.fromArray(ByteVector.SPECIES_64, a, offset);
        ByteVector lowerCase = c.or((byte) 0x20);
        VectorMask<Byte> ge_a = lowerCase.compare(UNSIGNED_GE, 'a');
        if (!c.compare(UNSIGNED_GE, '0').and(c.compare(UNSIGNED_LE, '9'))
                .or(ge_a.and(lowerCase.compare(UNSIGNED_LE, 'f')))
                .allTrue()) {
            return -1;
        }
        return lowerCase.sub((byte) '0')
                .sub((byte) ('a' - '0' - 10), ge_a)
                .castShape(IntVector.SPECIES_256, 0)
                .lanewise(LSHL, POWERS_OF_16_SHIFTS_BE)
                .reduceLanesToLong(ADD) & 0xffffffffL;
    }
}