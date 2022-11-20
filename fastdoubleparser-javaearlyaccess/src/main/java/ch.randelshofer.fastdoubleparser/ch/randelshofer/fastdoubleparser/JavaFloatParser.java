/*
 * @(#)JavaFloatParser.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code float} value; the supported syntax is compatible with
 * {@link Float#valueOf(String)}.
 * <p>
 * See {@link JavaDoubleParser} for a description of the supported grammar.
 */
public class JavaFloatParser {

    /**
     * Don't let anyone instantiate this class.
     */
    private JavaFloatParser() {

    }

    /**
     * Convenience method for calling {@link #parseFloat(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static float parseFloat(CharSequence str) throws NumberFormatException {
        return parseFloat(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@link CharSequence} and converts it
     * into a {@code float} value.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatingPointLiteral} in {@code str}
     * @param length the length of {@code FloatingPointLiteral} in {@code str}
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = new JavaFloatBitsFromCharSequence().parseFloatingPointLiteral(str, offset, length);
        return Float.intBitsToFloat((int) bitPattern);
    }

    /**
     * Convenience method for calling {@link #parseFloat(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static float parseFloat(byte[] str) throws NumberFormatException {
        return parseFloat(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code float} value.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first byte to parse
     * @param length The number of bytes to parse
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static float parseFloat(byte[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = new JavaFloatBitsFromByteArray().parseFloatingPointLiteral(str, offset, length);
        return Float.intBitsToFloat((int) bitPattern);
    }


    /**
     * Convenience method for calling {@link #parseFloat(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static float parseFloat(char[] str) throws NumberFormatException {
        return parseFloat(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code float} value.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static float parseFloat(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = new JavaFloatBitsFromCharArray().parseFloatingPointLiteral(str, offset, length);
        return Float.intBitsToFloat((int) bitPattern);
    }
}