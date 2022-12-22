/*
 * @(#)JsonDoubleParser.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code double} value; the supported syntax is compatible with
 * {@code number} in the JSON format specification.
 * <p>
 * <b>Syntax</b>
 * <p>
 * Numeric values that cannot be represented in the grammar below (such
 * as Infinity and NaN) are not permitted.
 * <pre>
 * number = [ minus ] int [ frac ] [ exp ]
 *
 * minus  = %x2D                        ; -
 * int    = zero / ( digit1-9 *DIGIT )
 * frac   = decimal-point 1*DIGIT
 * exp    = e [ minus / plus ] 1*DIGIT
 *
 * decimal-point = %x2E                 ; .
 * digit1-9      = %x31-39              ; 1-9
 * e             = %x65 / %x45          ; e E
 * plus          = %x2B                 ; +
 * zero          = %x30                 ; 0
 * </pre>
 * <p>
 * References:
 * <dl>
 *     <dt>IETF RFC 8259. The JavaScript Object Notation (JSON) Data Interchange
 *     Format, Chapter 6. Numbers</dt>
 *     <dd><a href="https://www.ietf.org/rfc/rfc8259.txt">www.ietf.org</a></dd>
 * </dl>
 */
public class JsonDoubleParser {

    private static final JsonDoubleBitsFromByteArray BYTE_ARRAY_PARSER = new JsonDoubleBitsFromByteArray();

    private static final JsonDoubleBitsFromCharArray CHAR_ARRAY_PARSER = new JsonDoubleBitsFromCharArray();

    private static final JsonDoubleBitsFromCharSequence CHARSEQUENCE_PARSER = new JsonDoubleBitsFromCharSequence();

    /**
     * Don't let anyone instantiate this class.
     */
    private JsonDoubleParser() {

    }

    /**
     * Convenience method for calling {@link #parseDouble(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static double parseDouble(CharSequence str) throws NumberFormatException {
        return parseDouble(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@link CharSequence} and converts it
     * into a {@code double} value.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatingPointLiteral} in {@code str}
     * @param length the length of {@code FloatingPointLiteral} in {@code str}
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static double parseDouble(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHARSEQUENCE_PARSER.parseNumber(str, offset, length);
        return Double.longBitsToDouble(bitPattern);
    }

    /**
     * Convenience method for calling {@link #parseDouble(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static double parseDouble(byte[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code double} value.
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
    public static double parseDouble(byte[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = BYTE_ARRAY_PARSER.parseNumber(str, offset, length);
        return Double.longBitsToDouble(bitPattern);
    }

    /**
     * Convenience method for calling {@link #parseDouble(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static double parseDouble(char[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code double} value.
     * <p>
     * See {@link JsonDoubleParser} for the syntax of {@code FloatingPointLiteral}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static double parseDouble(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_ARRAY_PARSER.parseNumber(str, offset, length);
        return Double.longBitsToDouble(bitPattern);
    }
}