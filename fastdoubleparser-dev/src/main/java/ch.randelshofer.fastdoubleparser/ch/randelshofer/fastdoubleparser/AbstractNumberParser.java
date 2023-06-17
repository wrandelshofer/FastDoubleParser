/*
 * @(#)AbstractNumberParser.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.util.Arrays;

abstract class AbstractNumberParser {
    /**
     * Message text for the {@link IllegalArgumentException} that is thrown
     * when offset or length are illegal
     */
    public static final String ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH = "offset < 0 or length > str.length";
    /**
     * Message text for the {@link NumberFormatException} that is thrown
     * when the syntax is illegal.
     */
    public static final String SYNTAX_ERROR = "illegal syntax";
    /**
     * Message text for the {@link NumberFormatException} that is thrown
     * when there are too many input digits.
     */
    public static final String VALUE_EXCEEDS_LIMITS = "value exceeds limits";
    /**
     * Special value in {@link #CHAR_TO_HEX_MAP} for
     * the decimal point character.
     */
    static final byte DECIMAL_POINT_CLASS = -4;
    /**
     * Special value in {@link #CHAR_TO_HEX_MAP} for
     * characters that are neither a hex digit nor
     * a decimal point character..
     */
    static final byte OTHER_CLASS = -1;
    /**
     * Includes all non-negative values of a {@code byte}, so that we only have
     * to check for byte values {@literal <} 0 before accessing this array.
     */
    static final byte[] CHAR_TO_HEX_MAP = new byte[256];
    static final short[] CHAR_TO_HEX_MAP2 = new short[256];

    static {
        Arrays.fill(CHAR_TO_HEX_MAP, OTHER_CLASS);
        for (char ch = '0'; ch <= '9'; ch++) {
            CHAR_TO_HEX_MAP[ch] = (byte) (ch - '0');
        }
        for (char ch = 'A'; ch <= 'F'; ch++) {
            CHAR_TO_HEX_MAP[ch] = (byte) (ch - 'A' + 10);
        }
        for (char ch = 'a'; ch <= 'f'; ch++) {
            CHAR_TO_HEX_MAP[ch] = (byte) (ch - 'a' + 10);
        }
        CHAR_TO_HEX_MAP['.'] = DECIMAL_POINT_CLASS;
        for (int i = 0; i < 256; i++) {
            byte value = CHAR_TO_HEX_MAP[i];
            CHAR_TO_HEX_MAP2[i] = value < 0 ? value : (short) (value << 4);
        }
    }

    /**
     * Returns the character at the specified index if index is &lt; endIndex;
     * otherwise returns 0.
     *
     * @param str      the string
     * @param i        the index
     * @param endIndex the end index
     * @return the character or 0
     */
    protected static byte charAt(byte[] str, int i, int endIndex) {
        return i < endIndex ? str[i] : 0;
    }

    /**
     * Returns the character at the specified index if index is &lt; endIndex;
     * otherwise returns 0.
     *
     * @param str      the string
     * @param i        the index
     * @param endIndex the end index
     * @return the character or 0
     */
    protected static char charAt(char[] str, int i, int endIndex) {
        return i < endIndex ? str[i] : 0;
    }

    /**
     * Returns the character at the specified index if index is &lt; endIndex;
     * otherwise returns 0.
     *
     * @param str      the string
     * @param i        the index
     * @param endIndex the end index
     * @return the character or 0
     */
    protected static char charAt(CharSequence str, int i, int endIndex) {
        return i < endIndex ? str.charAt(i) : 0;
    }

    /**
     * Looks the character up in the {@link #CHAR_TO_HEX_MAP} returns
     * a value &lt; 0 if the character is not in the map.
     * <p>
     * Returns -4 if the character is a decimal point.
     *
     * @param ch a character
     * @return the hex value or a value &lt; 0.
     */
    protected static byte lookupHex(byte ch) {
        return CHAR_TO_HEX_MAP[ch & 0xff];
    }

    protected static int lookupHex2(byte high, byte low) {
        return CHAR_TO_HEX_MAP2[high & 0xff] | CHAR_TO_HEX_MAP[low & 0xff];
    }
    /**
     * Looks the character up in the {@link #CHAR_TO_HEX_MAP} returns
     * a value &lt; 0 if the character is not in the map.
     * <p>
     * Returns -1 if the character code is &gt; 255.
     * <p>
     * Returns -4 if the character is a decimal point.
     *
     * @param ch a character
     * @return the hex value or a value &lt; 0.
     */
    protected static byte lookupHex(char ch) {
        // The branchy code is faster than the branch-less code.
        // Branch-less code: return CHAR_TO_HEX_MAP[ch & 0xff] | (127 - ch) >> 31;
        // Branch-less code: return CHAR_TO_HEX_MAP[(ch|((127-ch)>>31))&0xff];
        // Branch-less code: return CHAR_TO_HEX_MAP[ch<128?ch:0];
        return ch < 128 ? CHAR_TO_HEX_MAP[ch] : -1;
    }

    protected static int lookupHex2(char high, char low) {
        return (high | low) < 128 ? CHAR_TO_HEX_MAP2[high] | CHAR_TO_HEX_MAP[low] : -1;
    }
    /**
     * Checks the bounds and returns the end index (exclusive) of the data in the array.
     *
     * @param size           length of array (Must be in the range from 0 to max length of
     *                       a Java array. This value is not checked, because this is an internal API!)
     * @param offset         start-index of data into array (Must be non-negative and smaller than size)
     * @param length         length of data (Must be non-negative and smaller than size - offset)
     * @param maxInputLength maximal input length that can yield a legal value
     * @return offset + length
     */
    protected static int checkBounds(int size, int offset, int length, int maxInputLength) {
        if (length > maxInputLength) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
        return checkBounds(size, offset, length);
    }

    /**
     * Checks the bounds and returns the end index (exclusive) of the data in the array.
     *
     * @param size   length of array (Must be in the range from 0 to max length of
     *               a Java array. This value is not checked, because this is an internal API!)
     * @param offset start-index of data into array (Must be non-negative and smaller than size)
     * @param length length of data (Must be non-negative and smaller than size - offset)
     * @return offset + length
     */
    protected static int checkBounds(int size, int offset, int length) {
        if ((offset | length | size - length - offset) < 0) { // tricky way of testing multiple negative values at once
            throw new IllegalArgumentException(ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
        }
        return length + offset;
    }
}
