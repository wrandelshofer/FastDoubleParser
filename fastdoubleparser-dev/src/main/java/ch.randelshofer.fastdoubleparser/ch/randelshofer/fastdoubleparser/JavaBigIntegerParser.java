/*
 * @(#)JavaBigIntegerParser.java
 * Copyright © 2022 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;

/**
 * Parses a {@link BigInteger} value; the supported syntax is a super-set of
 * the syntax supported by {@link BigInteger#BigInteger(String)}.
 * <p>
 * <b>Syntax</b>
 * <p>
 * Formal specification of the grammar:
 * <blockquote>
 * <dl>
 * <dt><i>BigIntegerLiteral:</i></dt>
 * <dd><i>[Sign] Digits</i></dd>
 * <dd><i>[Sign]</i> {@code 0x} <i>[HexDigits]</i>
 * <dd><i>[Sign]</i> {@code 0X} <i>[HexDigits]</i>
 * </dl>
 * <dl>
 * <dt><i>Digits:</i>
 * <dd><i>Digit {Digit}</i>
 * </dl>
 * <dl>
 * <dt><i>HexDigits:</i>
 * <dd><i>HexDigit {HexDigit}</i>
 * </dl>
 * <dl>
 * <dt><i>Digit:</i>
 * <dd><i>(one of)</i>
 * <dd>{@code 0 1 2 3 4 5 6 7 8 9}
 * </dl>
 * <dl>
 * <dt><i>HexDigit:</i>
 * <dd><i>(one of)</i>
 * <dd>{@code 0 1 2 3 4 5 6 7 8 9 a b c d e f A B C D E F}
 * </dl>
 * </blockquote>
 * <p>
 * Character lengths accepted by {@link BigInteger#BigInteger(String)}:
 * <ul>
 *     <li>{@code Significand}: 1 to 1,292,782,621 decimal digits.
 * <p>
 *     The resulting value must fit into {@code 2^31 - 1} bits. The decimal
 *     representation of the value {@code 2^31 - 1} has 646,456,993 digits.
 *     Therefore an input String can only contain up to that many significant
 *     digits - the remaining digits must be leading zeroes.
 *     </li>
 * </ul>
 */
public class JavaBigIntegerParser {

    private static final JavaBigIntegerFromByteArray BYTE_ARRAY_PARSER = new JavaBigIntegerFromByteArray();

    private static final JavaBigIntegerFromCharArray CHAR_ARRAY_PARSER = new JavaBigIntegerFromCharArray();

    private static final JavaBigIntegerFromCharSequence CHARSEQUENCE_PARSER = new JavaBigIntegerFromCharSequence();

    /**
     * Don't let anyone instantiate this class.
     */
    private JavaBigIntegerParser() {
    }

    /**
     * Convenience method for calling {@link #parseBigInteger(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static BigInteger parseBigInteger(CharSequence str) {
        return parseBigInteger(str, 0, str.length());
    }

    /**
     * Convenience method for calling {@link #parseBigInteger(byte[], int, int)}.
     *
     * @param str    the string to be parsed
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static BigInteger parseBigInteger(CharSequence str, int offset, int length) {
        return CHARSEQUENCE_PARSER.parseBigIntegerLiteral(str, offset, length, false);
    }

    /**
     * Convenience method for calling {@link #parseBigInteger(byte[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static BigInteger parseBigInteger(byte[] str) {
        return parseBigInteger(str, 0, str.length);
    }

    /**
     * Parses a {@code BigIntegerLiteral} from a {@code byte}-Array and converts it
     * into a {@link BigInteger} value.
     * <p>
     * See {@link JsonDoubleParser} for the syntax of {@code FloatingPointLiteral}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static BigInteger parseBigInteger(byte[] str, int offset, int length) {
        return BYTE_ARRAY_PARSER.parseBigIntegerLiteral(str, offset, length, false);
    }

    /**
     * Convenience method for calling {@link #parseBigInteger(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static BigInteger parseBigInteger(char[] str) {
        return parseBigInteger(str, 0, str.length);
    }

    /**
     * Parses a {@code BigIntegerLiteral} from a {@code char}-Array and converts it
     * into a {@link BigInteger} value.
     * <p>
     * See {@link JsonDoubleParser} for the syntax of {@code FloatingPointLiteral}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static BigInteger parseBigInteger(char[] str, int offset, int length) {
        return CHAR_ARRAY_PARSER.parseBigIntegerLiteral(str, offset, length, false);
    }

    /**
     * Convenience method for calling {@link #parallelParseBigInteger(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static BigInteger parallelParseBigInteger(CharSequence str) {
        return parallelParseBigInteger(str, 0, str.length());
    }

    /**
     * Convenience method for calling {@link #parallelParseBigInteger(byte[], int, int)}.
     *
     * @param str the string to be parsed
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static BigInteger parallelParseBigInteger(CharSequence str, int offset, int length) {
        return CHARSEQUENCE_PARSER.parseBigIntegerLiteral(str, offset, length, true);
    }

    /**
     * Convenience method for calling {@link #parallelParseBigInteger(byte[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static BigInteger parallelParseBigInteger(byte[] str) {
        return parallelParseBigInteger(str, 0, str.length);
    }

    /**
     * Parses a {@code BigIntegerLiteral} from a {@code byte}-Array and converts it
     * into a {@link BigInteger} value.
     * <p>
     * See {@link JsonDoubleParser} for the syntax of {@code FloatingPointLiteral}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static BigInteger parallelParseBigInteger(byte[] str, int offset, int length) {
        return BYTE_ARRAY_PARSER.parseBigIntegerLiteral(str, offset, length, true);
    }

    /**
     * Convenience method for calling {@link #parallelParseBigInteger(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public static BigInteger parallelParseBigInteger(char[] str) {
        return parallelParseBigInteger(str, 0, str.length);
    }

    /**
     * Parses a {@code BigIntegerLiteral} from a {@code char}-Array and converts it
     * into a {@link BigInteger} value.
     * <p>
     * See {@link JsonDoubleParser} for the syntax of {@code FloatingPointLiteral}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public static BigInteger parallelParseBigInteger(char[] str, int offset, int length) {
        return CHAR_ARRAY_PARSER.parseBigIntegerLiteral(str, offset, length, true);
    }
}
