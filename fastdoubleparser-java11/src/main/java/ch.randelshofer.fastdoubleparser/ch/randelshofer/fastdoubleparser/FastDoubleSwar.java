/*
 * @(#)FastDoubleSwar.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

/**
 * This class provides methods for parsing multiple characters at once using
 * the "SIMD with a register" (SWAR) technique.
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
class FastDoubleSwar {

    private final static VarHandle readLongLE =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private final static VarHandle readIntLE =
            MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);
    private final static VarHandle readIntBE =
            MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private final static VarHandle readLongBE =
            MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    /**
     * Checks if '0' <= c && c <= '9'.
     *
     * @param c a character
     * @return true if c is a digit
     */
    protected static boolean isDigit(char c) {
        // We take advantage of the fact that char is an unsigned numeric type:
        // subtracted values wrap around.
        return (char) (c - '0') <= (char) ('9' - '0');
    }

    /**
     * Checks if '0' <= c && c <= '9'.
     *
     * @param c a character
     * @return true if c is a digit
     */
    protected static boolean isDigit(byte c) {
        // We check if '0' <= c && c <= '9'.
        // We take advantage of the fact that char is an unsigned numeric type:
        // subtracted values wrap around.
        return (char) (c - '0') <= (char) ('9' - '0');
    }

    public static boolean isEightDigits(byte[] a, int offset) {
        return isEightDigitsUtf8((long) readLongLE.get(a, offset));
    }

    /**
     * Checks if the string contains eight digits at the specified
     * offset.
     *
     * @param a      a string
     * @param offset offset into string
     * @return true if eight digits
     * @throws IndexOutOfBoundsException if offset is larger than 2^29.
     */
    public static boolean isEightDigits(char[] a, int offset) {
        long first = a[offset]
                | (long) a[offset + 1] << 16
                | (long) a[offset + 2] << 32
                | (long) a[offset + 3] << 48;
        long second = a[offset + 4]
                | (long) a[offset + 5] << 16
                | (long) a[offset + 6] << 32
                | (long) a[offset + 7] << 48;
        return isEightDigitsUtf16(first, second);
    }

    public static boolean isEightDigits(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; i++) {
            char ch = a.charAt(i + offset);
            success &= isDigit(ch);
        }
        return success;
    }

    public static boolean isEightDigitsUtf16(long first, long second) {
        long fval = first - 0x0030_0030_0030_0030L;
        long sval = second - 0x0030_0030_0030_0030L;

        // Create a predicate for all bytes which are smaller than '0' (0x0030)
        // or greater than '9' (0x0039).
        // We have 0x007f - 0x0039 = 0x0046.
        // The predicate is true if the hsb of a byte is set: (predicate & 0xff80) != 0.
        long fpre = first + 0x0046_0046_0046_0046L | fval;
        long spre = second + 0x0046_0046_0046_0046L | sval;
        return ((fpre | spre) & 0xff80_ff80_ff80_ff80L) == 0L;
    }

    public static boolean isEightDigitsUtf8(long chunk) {
        long val = chunk - 0x3030303030303030L;
        long predicate = ((chunk + 0x4646464646464646L) | val) & 0x8080808080808080L;
        return predicate == 0L;
    }

    public static boolean isEightZeroes(byte[] a, int offset) {
        return isEightZeroesUtf8((long) readLongLE.get(a, offset));
    }

    public static boolean isEightZeroes(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; i++) {
            success &= '0' == a.charAt(i + offset);
        }
        return success;
    }

    /**
     * Checks if the string contains eight zeroes at the specified
     * offset.
     *
     * @param a      a string
     * @param offset offset into string
     * @return true if eight digits
     * @throws IndexOutOfBoundsException if offset is larger than 2^29.
     */
    public static boolean isEightZeroes(char[] a, int offset) {
        long first = a[offset]
                | (long) a[offset + 1] << 16
                | (long) a[offset + 2] << 32
                | (long) a[offset + 3] << 48;
        long second = a[offset + 4]
                | (long) a[offset + 5] << 16
                | (long) a[offset + 6] << 32
                | (long) a[offset + 7] << 48;
        return isEightZeroesUtf16(first, second);
    }

    public static boolean isEightZeroesUtf16(long first, long second) {
        return first == 0x0030_0030_0030_0030L
                && second == 0x0030_0030_0030_0030L;
    }

    public static boolean isEightZeroesUtf8(long chunk) {
        return chunk == 0x3030303030303030L;
    }

    public static int parseEightDigits(char[] a, int offset) {
        long first = a[offset]
                | (long) a[offset + 1] << 16
                | (long) a[offset + 2] << 32
                | (long) a[offset + 3] << 48;
        long second = a[offset + 4]
                | (long) a[offset + 5] << 16
                | (long) a[offset + 6] << 32
                | (long) a[offset + 7] << 48;
        return FastDoubleSwar.parseEightDigitsUtf16(first, second);
    }

    public static int parseEightDigits(CharSequence str, int offset) {
        long first = str.charAt(offset)
                | (long) str.charAt(offset + 1) << 16
                | (long) str.charAt(offset + 2) << 32
                | (long) str.charAt(offset + 3) << 48;
        long second = str.charAt(offset + 4)
                | (long) str.charAt(offset + 5) << 16
                | (long) str.charAt(offset + 6) << 32
                | (long) str.charAt(offset + 7) << 48;
        return FastDoubleSwar.parseEightDigitsUtf16(first, second);
    }

    public static int parseEightDigits(byte[] a, int offset) {
        return parseEightDigitsUtf8((long) readLongLE.get(a, offset));
    }

    public static int parseEightDigitsUtf16(long first, long second) {
        long fval = first - 0x0030_0030_0030_0030L;
        long sval = second - 0x0030_0030_0030_0030L;
        return (int) (sval * 0x03e8_0064_000a_0001L >>> 48)
                + (int) (fval * 0x03e8_0064_000a_0001L >>> 48) * 10000;
    }

    public static int parseEightDigitsUtf8(long chunk) {
        // Subtract the character '0' from all characters.
        long val = chunk - 0x3030303030303030L;

        // The last 2 multiplications are independent of each other.
        long mask = 0xff_000000ffL;
        long mul1 = 100 + (100_0000L << 32);
        long mul2 = 1 + (1_0000L << 32);
        val = val * 10 + (val >>> 8);// same as: val = val * (1 + (10 << 8)) >>> 8;
        val = (val & mask) * mul1 + (val >>> 16 & mask) * mul2 >>> 32;
        return (int) val;
    }

    public static int parseUpTo7Digits(byte[] str, int from, int to) {
        int result = 0;
        for (; from < to; from++) {
            result = 10 * (result) + str[from] - '0';
        }
        return result;
    }

    public static int parseUpTo7Digits(char[] str, int from, int to) {
        int result = 0;
        for (; from < to; from++) {
            result = 10 * (result) + str[from] - '0';
        }
        return result;
    }

    public static int parseUpTo7Digits(CharSequence str, int from, int to) {
        int result = 0;
        for (; from < to; from++) {
            result = 10 * (result) + str.charAt(from) - '0';
        }
        return result;
    }

    public static int readIntBE(byte[] a, int offset) {
        return (int) readIntBE.get(a, offset);
    }

    public static long readLongBE(byte[] a, int offset) {
        return (long) readLongBE.get(a, offset);
    }

    public static long readLongLE(byte[] a, int offset) {
        return (long) readLongLE.get(a, offset);
    }

    /**
     * Tries to parse eight decimal digits from a char array using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param a      contains 8 utf-16 characters starting at offset
     * @param offset the offset into the array
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 hex digits
     * @throws IndexOutOfBoundsException if offset is larger than 2^
     */

    public static int tryToParseEightDigits(char[] a, int offset) {
        long first = a[offset]
                | (long) a[offset + 1] << 16
                | (long) a[offset + 2] << 32
                | (long) a[offset + 3] << 48;
        long second = a[offset + 4]
                | (long) a[offset + 5] << 16
                | (long) a[offset + 6] << 32
                | (long) a[offset + 7] << 48;
        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    public static int tryToParseEightDigits(byte[] a, int offset) {
        return FastDoubleSwar.tryToParseEightDigitsUtf8((long) readLongLE.get(a, offset));
    }

    /**
     * Tries to parse eight digits at once using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param str    a character sequence
     * @param offset the index of the first character in the character sequence
     * @return the parsed digits or -1
     */
    public static int tryToParseEightDigits(CharSequence str, int offset) {
        long first = str.charAt(offset)
                | (long) str.charAt(offset + 1) << 16
                | (long) str.charAt(offset + 2) << 32
                | (long) str.charAt(offset + 3) << 48;
        long second = str.charAt(offset + 4)
                | (long) str.charAt(offset + 5) << 16
                | (long) str.charAt(offset + 6) << 32
                | (long) str.charAt(offset + 7) << 48;
        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    /**
     * Tries to parse eight decimal digits at once using the
     * 'SIMD within a register technique' (SWAR).
     *
     * <pre>{@literal
     * char[] chars = ...;
     * long first  = chars[0]|(chars[1]<<16)|(chars[2]<<32)|(chars[3]<<48);
     * long second = chars[4]|(chars[5]<<16)|(chars[6]<<32)|(chars[7]<<48);
     * }</pre>
     *
     * @param first  the first four characters in big endian order
     * @param second the second four characters in big endian order
     * @return the parsed digits or -1
     */
    public static int tryToParseEightDigitsUtf16(long first, long second) {
        long fval = first - 0x0030_0030_0030_0030L;
        long sval = second - 0x0030_0030_0030_0030L;

        // Create a predicate for all bytes which are smaller than '0' (0x0030)
        // or greater than '9' (0x0039).
        // We have 0x007f - 0x0039 = 0x0046.
        // The predicate is true if the hsb of a byte is set: (predicate & 0xff80) != 0.
        long fpre = first + 0x0046_0046_0046_0046L | fval;
        long spre = second + 0x0046_0046_0046_0046L | sval;
        if (((fpre | spre) & 0xff80_ff80_ff80_ff80L) != 0L) {
            return -1;
        }

        return (int) (sval * 0x03e8_0064_000a_0001L >>> 48)
                + (int) (fval * 0x03e8_0064_000a_0001L >>> 48) * 10000;
    }

    /**
     * Tries to parse eight decimal digits from a byte array using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param a      contains 8 ascii characters
     * @param offset the offset of the first character in {@code a}
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 digits
     */
    public static int tryToParseEightDigitsUtf8(byte[] a, int offset) {
        return tryToParseEightDigitsUtf8((long) readLongLE.get(a, offset));
    }

    /**
     * Tries to parse eight digits from a long using the
     * 'SIMD within a register technique' (SWAR).
     *
     * <pre>{@literal
     * byte[] bytes = ...;
     * long value  = ((bytes[7]&0xffL)<<56)
     *             | ((bytes[6]&0xffL)<<48)
     *             | ((bytes[5]&0xffL)<<40)
     *             | ((bytes[4]&0xffL)<<32)
     *             | ((bytes[3]&0xffL)<<24)
     *             | ((bytes[2]&0xffL)<<16)
     *             | ((bytes[1]&0xffL)<< 8)
     *             |  (bytes[0]&0xffL);
     * }</pre>
     *
     * @param chunk contains 8 ascii characters in little endian order
     * @return the parsed number, or a value &lt; 0 if not all characters are
     * digits.
     */
    public static int tryToParseEightDigitsUtf8(long chunk) {
        // Subtract the character '0' from all characters.
        long val = chunk - 0x3030303030303030L;

        // Create a predicate for all bytes which are greater than '0' (0x30).
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        long predicate = ((chunk + 0x4646464646464646L) | val) & 0x8080808080808080L;
        if (predicate != 0L) {
            return -1;
        }

        // The last 2 multiplications are independent of each other.
        long mask = 0xff_000000ffL;
        long mul1 = 100 + (100_0000L << 32);
        long mul2 = 1 + (1_0000L << 32);
        val = val * 10 + (val >>> 8);// same as: val = val * (1 + (10 << 8)) >>> 8;
        val = (val & mask) * mul1 + (val >>> 16 & mask) * mul2 >>> 32;
        return (int) val;
    }

    /**
     * Tries to parse eight digits at once using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param str    a character sequence
     * @param offset the index of the first character in the character sequence
     * @return the parsed digits or -1
     */
    public static long tryToParseEightHexDigits(CharSequence str, int offset) {
        long first = (long) str.charAt(offset) << 48
                | (long) str.charAt(offset + 1) << 32
                | (long) str.charAt(offset + 2) << 16
                | (long) str.charAt(offset + 3);

        long second = (long) str.charAt(offset + 4) << 48
                | (long) str.charAt(offset + 5) << 32
                | (long) str.charAt(offset + 6) << 16
                | (long) str.charAt(offset + 7);

        return FastDoubleSwar.tryToParseEightHexDigitsUtf16(first, second);
    }

    /**
     * Tries to parse eight hex digits from a char array using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param chars  contains 8 utf-16 characters starting at offset
     * @param offset the offset into the array
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 hex digits
     */
    public static long tryToParseEightHexDigits(char[] chars, int offset) {
        // Performance: We extract the chars in two steps so that we
        //              can benefit from out of order execution in the CPU.
        long first = (long) chars[offset] << 48
                | (long) chars[offset + 1] << 32
                | (long) chars[offset + 2] << 16
                | (long) chars[offset + 3];

        long second = (long) chars[offset + 4] << 48
                | (long) chars[offset + 5] << 32
                | (long) chars[offset + 6] << 16
                | (long) chars[offset + 7];

        return FastDoubleSwar.tryToParseEightHexDigitsUtf16(first, second);
    }

    /**
     * Tries to parse eight hex digits from a byte array using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param a      contains 8 ascii characters
     * @param offset the offset of the first character in {@code a}
     *               returns a negative value if {@code value} does not contain 8 digits
     */
    public static long tryToParseEightHexDigits(byte[] a, int offset) {
        return tryToParseEightHexDigitsUtf8((long) readLongBE.get(a, offset));
    }

    /**
     * Tries to parse eight hex digits from two longs using the
     * 'SIMD within a register technique' (SWAR).
     *
     * <pre>{@code
     * char[] chars = ...;
     * long first  = (long) chars[0] << 48
     *             | (long) chars[1] << 32
     *             | (long) chars[2] << 16
     *             | (long) chars[3];
     *
     * long second = (long) chars[4] << 48
     *             | (long) chars[5] << 32
     *             | (long) chars[6] << 16
     *             | (long) chars[7];
     * }</pre>
     *
     * @param first  contains 4 utf-16 characters in big endian order
     * @param second contains 4 utf-16 characters in big endian order
     * @return the parsed number,
     * returns a negative value if the two longs do not contain 8 hex digits
     */
    public static long tryToParseEightHexDigitsUtf16(long first, long second) {
        long lfirst = tryToParseFourHexDigitsUtf16(first);
        long lsecond = tryToParseFourHexDigitsUtf16(second);
        return (lfirst << 16) | lsecond;
    }

    /**
     * Tries to parse eight digits from a long using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param chunk contains 8 ascii characters in big endian order
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 digits
     */
    public static long tryToParseEightHexDigitsUtf8(long chunk) {
        // The following code is based on the technique presented in the paper
        // by Leslie Lamport.

        // We can convert upper case characters to lower case by setting the 0x20 bit.
        // (This does not have an impact on decimal digits, which is very handy!).
        // Subtract character '0' (0x30) from each of the eight characters
        long vec = (chunk | 0x20_20_20_20_20_20_20_20L) - 0x30_30_30_30_30_30_30_30L;

        // Create a predicate for all bytes which are greater than '9'-'0' (0x09).
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        long gt_09 = vec + (0x09_09_09_09_09_09_09_09L ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        gt_09 &= 0x80_80_80_80_80_80_80_80L;

        // Create a predicate for all bytes which are greater or equal 'a'-'0' (0x30).
        // The predicate is true if the hsb of a byte is set.
        long ge_30 = vec + (0x30303030_30303030L ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        ge_30 &= 0x80_80_80_80_80_80_80_80L;

        // Create a predicate for all bytes which are smaller equal than 'f'-'0' (0x37).
        long le_37 = 0x37_37_37_37_37_37_37_37L + (vec ^ 0x7f_7f_7f_7f_7f_7f_7f_7fL);
        // we don't need to 'and' with 0x80…L here, because we 'and' this with ge_30 anyway.
        //le_37 &= 0x80_80_80_80_80_80_80_80L;


        // If a character is greater than '9' then it must be greater equal 'a'
        // and smaller  'f'.
        if (gt_09 != (ge_30 & le_37)) {
            return -1;
        }

        // Expand the predicate to a byte mask
        long gt_09mask = (gt_09 >>> 7) * 0xffL;

        // Subtract 'a'-'0'+10 (0x27) from all bytes that are greater than 0x09.
        long v = vec & ~gt_09mask | vec - (0x27272727_27272727L & gt_09mask);

        // Compact all nibbles
        //return Long.compress(v, 0x0f0f0f0f_0f0f0f0fL);// since Java 19
        long v2 = v | v >>> 4;
        long v3 = v2 & 0x00ff00ff_00ff00ffL;
        long v4 = v3 | v3 >>> 8;
        long v5 = ((v4 >>> 16) & 0xffff_0000L) | v4 & 0xffffL;
        return v5;
    }

    public static int tryToParseFourDigits(char[] a, int offset) {
        long first = a[offset]
                | (long) a[offset + 1] << 16
                | (long) a[offset + 2] << 32
                | (long) a[offset + 3] << 48;
        return FastDoubleSwar.tryToParseFourDigitsUtf16(first);
    }

    public static int tryToParseFourDigits(CharSequence str, int offset) {
        long first = str.charAt(offset)
                | (long) str.charAt(offset + 1) << 16
                | (long) str.charAt(offset + 2) << 32
                | (long) str.charAt(offset + 3) << 48;

        return FastDoubleSwar.tryToParseFourDigitsUtf16(first);
    }

    public static int tryToParseFourDigits(byte[] a, int offset) {
        return tryToParseFourDigitsUtf8((int) readIntLE.get(a, offset));
    }

    public static int tryToParseFourDigitsUtf16(long first) {
        long fval = first - 0x0030_0030_0030_0030L;

        // Create a predicate for all bytes which are smaller than '0' (0x0030)
        // or greater than '9' (0x0039).
        // We have 0x007f - 0x0039 = 0x0046.
        // The predicate is true if the hsb of a byte is set: (predicate & 0xff80) != 0.
        long fpre = first + 0x0046_0046_0046_0046L | fval;
        if ((fpre & 0xff80_ff80_ff80_ff80L) != 0L) {
            return -1;
        }

        return (int) (fval * 0x03e8_0064_000a_0001L >>> 48);
    }

    public static int tryToParseFourDigitsUtf8(int chunk) {
        // Create a predicate for all bytes which are greater than '0' (0x30).
        // The predicate is true if the hsb of a byte is set: (predicate & 0x80) != 0.
        int val = chunk - 0x30303030;
        int predicate = ((chunk + 0x46464646) | val) & 0x80808080;
        if (predicate != 0L) {
            return -1;//~(Integer.numberOfTrailingZeros(predicate)>>3);
        }

        // The last 2 multiplications are independent of each other.
        val = val * (1 + (10 << 8)) >>> 8;
        val = (val & 0xff) * 100 + ((val & 0xff0000) >> 16);
        return val;
    }

    /**
     * Tries to parse four hex digits from a long using the
     * 'SIMD within a register technique' (SWAR).
     *
     * @param chunk contains 4 utf-16 characters in big endian order
     * @return the parsed number,
     * returns a negative value if {@code value} does not contain 8 digits
     */
    public static long tryToParseFourHexDigitsUtf16(long chunk) {
        // The following code is based on the technique presented in the paper
        // by Leslie Lamport.


        // Subtract character '0' (0x0030) from each of the four characters
        long vec = chunk - 0x0030_0030_0030_0030L;

        // Create a predicate for all bytes which are greater than '9'-'0' (0x0009).
        // The predicate is true if the hsb of a byte is set: (predicate & 0xa000) != 0.
        long gt_09 = vec + (0x0009_0009_0009_0009L ^ 0x7fff_7fff_7fff_7fffL);
        gt_09 = gt_09 & 0x8000_8000_8000_8000L;
        // Create a predicate for all bytes which are greater or equal 'a'-'0' (0x0030).
        // The predicate is true if the hsb of a byte is set.
        long ge_30 = vec + (0x0030_0030_0030_0030L ^ 0x7fff_7fff_7fff_7fffL);
        ge_30 = ge_30 & 0x8000_8000_8000_8000L;

        // Create a predicate for all bytes which are smaller equal than 'f'-'0' (0x0037).
        long le_37 = 0x0037_0037_0037_0037L + (vec ^ 0x7fff_7fff_7fff_7fffL);
        // Not needed, because we are going to and this value with ge_30 anyway.
        //le_37 = le_37 & 0x8000_8000_8000_8000L;


        // If a character is greater than '9' then it must be greater equal 'a'
        // and smaller equal 'f'.
        if (gt_09 != (ge_30 & le_37)) {
            return -1;
        }

        // Expand the predicate to a char mask
        long gt_09mask = (gt_09 >>> 15) * 0xffffL;

        // Subtract 'a'-'0'+10 (0x0027) from all bytes that are greater than 0x09.
        long v = vec & ~gt_09mask | vec - (0x0027_0027_0027_0027L & gt_09mask);

        // Compact all nibbles
        long v2 = v | v >>> 12;
        long v5 = (v2 | v2 >>> 24) & 0xffffL;

        return v5;
    }

    public static int tryToParseUpTo7Digits(byte[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        for (; from < to; from++) {
            byte ch = str[from];
            success &= isDigit(ch);
            result = 10 * (result) + ch - '0';
        }
        return success ? result : -1;
    }

    public static int tryToParseUpTo7Digits(char[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        for (; from < to; from++) {
            char ch = str[from];
            success &= isDigit(ch);
            result = 10 * (result) + ch - '0';
        }
        return success ? result : -1;
    }

    public static int tryToParseUpTo7Digits(CharSequence str, int from, int to) {
        int result = 0;
        boolean success = true;
        for (; from < to; from++) {
            char ch = str.charAt(from);
            success &= isDigit(ch);
            result = 10 * (result) + ch - '0';
        }
        return success ? result : -1;
    }

    public static void writeIntBE(byte[] a, int offset, int value) {
        readIntBE.set(a, offset, value);
    }

    public static void writeLongBE(byte[] a, int offset, long value) {
        readLongBE.set(a, offset, value);
    }
}