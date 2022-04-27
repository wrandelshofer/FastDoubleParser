/*
 * @(#)FastDoubleParser.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.nio.charset.StandardCharsets;

/**
 * Parses a double from a byte array.
 */
public class FastDoubleParserFromByteArray {

    public static final DoubleFromByteArrayParser DP = new DoubleFromByteArrayParser();

    /**
     * Don't let anyone instantiate this class.
     */
    private FastDoubleParserFromByteArray() {

    }
    /**
     * Convenience method for writing
     * {@code #parseDouble(str.getBytes(StandardCharsets.ISO_8859_1)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     * @see #parseDouble(byte[], int, int)
     */
    public static double parseDouble(String str) throws NumberFormatException {
        return parseDouble(str.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Convenience method for calling {@link #parseDouble(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static double parseDouble(byte[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a {@code double} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @param off The index of the first byte to parse
     * @param len The number of bytes to parse
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static double parseDouble(byte[] str, int off, int len) throws NumberFormatException {
        return DP.parseDouble(str, off,len);
    }
}