/*
 * @(#)FastDoubleParser.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Parses a double from a char array.
 */
public class FastDoubleParserFromCharArray {

    /**
     * Don't let anyone instantiate this class.
     */
    private FastDoubleParserFromCharArray() {

    }

    /**
     * Convenience method for writing
     * {@code #parseDouble(str.toCharArray()}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     * @see #parseDouble(char[], int, int)
     */
    public static double parseDouble(String str) throws NumberFormatException {
        return parseDouble(str.toCharArray());
    }

    /**
     * Convenience method for calling {@link #parseDouble(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static double parseDouble(char[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a {@code double} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @param off The index of the first character to parse
     * @param len The number of characters to parse
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static double parseDouble(char[] str, int off, int len) throws NumberFormatException {
        return new DoubleFromCharArrayParser().parseDouble(str,off,len);
    }


}