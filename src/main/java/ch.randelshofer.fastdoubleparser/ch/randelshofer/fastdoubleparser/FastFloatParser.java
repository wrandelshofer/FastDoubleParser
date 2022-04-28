/*
 * @(#)FastFloatParser2.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Provides static method for parsing a {@code float} from a
 * {@link CharSequence}, {@code char} array or {@code byte} array.
 */
public class FastFloatParser {

    /**
     * Don't let anyone instantiate this class.
     */
    private FastFloatParser() {

    }

    /**
     * Convenience method for calling {@link #parseFloat(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed float value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(CharSequence str) throws NumberFormatException {
        return parseFloat(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatValue} from a {@link CharSequence} and converts it
     * into a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatValue} in {@code str}
     * @param length the length of {@code FloatValue} in {@code str}
     * @return the parsed float value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
        return FloatFromCharSequence.INSTANCE.parseFloat(str, offset, length);
    }

    /**
     * Convenience method for calling {@link #parseFloat(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed float value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(byte[] str) throws NumberFormatException {
        return parseFloat(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @param off The index of the first byte to parse
     * @param len The number of bytes to parse
     * @return the parsed float value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(byte[] str, int off, int len) throws NumberFormatException {
        return FloatFromByteArray.INSTANCE.parseFloat(str, off, len);
    }


    /**
     * Convenience method for calling {@link #parseFloat(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed float value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(char[] str) throws NumberFormatException {
        return parseFloat(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @param off The index of the first character to parse
     * @param len The number of characters to parse
     * @return the parsed float value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(char[] str, int off, int len) throws NumberFormatException {
        return FloatFromCharArray.INSTANCE.parseFloat(str, off, len);
    }

}