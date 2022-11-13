/*
 * @(#)FastDoubleParser2.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides static method for parsing a {@code double} from a
 * {@link CharSequence}, {@code char} array or {@code byte} array.
 * <p>
 * <b>Syntax</b>
 * <p>
 * Leading and trailing whitespace characters in {@code str} are ignored.
 * Whitespace is removed as if by the {@link String#trim()} method;
 * that is, characters in the range [U+0000,U+0020].
 * <p>
 * The rest of {@code str} should constitute a Java {@code FloatingPointLiteral}
 * as described by the lexical syntax rules shown below:
 * <blockquote>
 * <dl>
 * <dt><i>BigDecimalString:</i></dt>
 * <dd><i>[Sign] Significand [Exponent]</i></dd>
 * </dl>
 *
 * <dl>
 * <dt><i>Sign:</i>
 * <dd><i>+</i>
 * <dd><i>-</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Significand:</i>
 * <dd><i>IntegerPart {@code .} [FractionPart]</i>
 * <dd><i>{@code .} FractionPart</i>
 * <dd><i>IntegerPart</i>
 * </dl>
 *
 * <dl>
 * <dt><i>IntegerPart:</i>
 * <dd><i>Digits</i>
 * </dl>
 *
 * <dl>
 * <dt><i>FractionPart:</i>
 * <dd><i>Digits</i>
 * </dl>
 *
 * <dl>
 * <dt><i>DecExponent:</i>
 * <dd><i>ExponentIndicator SignedInteger</i>
 * </dl>
 *
 * <dl>
 * <dt><i>ExponentIndicator:</i>
 * <dd><i>e</i>
 * <dd><i>E</i>
 * </dl>
 *
 * <dl>
 * <dt><i>SignedInteger:</i>
 * <dd><i>[Sign] Digits</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Digits:</i>
 * <dd><i>Digit {Digit}</i>
 * </dl>
 * <p>
 * Expected character lengths for values produced by {@link BigDecimal#toString}:
 * <ul>
 *     <li>{@code Significand}: 1 to 536_870_919 digits.
 *     <p>
 *     The significand of a {@link BigDecimal} uses a {@link BigInteger}
 *     to represent its significand. A {@link BigInteger} can work with up to
 *     {@code (1L << 28) - 4 = 2_147_483_616} significant bytes. A decimal digit
 *     needs about {@code 3.322265625} bits. This would allow for 5_171_076_943
 *     digits.
 *     <p>
 *     However the constructor {@link BigInteger#BigInteger(String)}
 *     checks if the number of bits is less than {@code (1L << 32) - 31}.
 *     This would allow for 1_292_782_621 decimal digits.
 *     <p>
 *     Yet a {@link BigDecimal} can only perform computations when
 *     the significand has no more than 536_870_919 decimal digits.
 *     </li>
 *     <li>{@code SignedInteger} in exponent: 1 to 10 digits. Exponents
 *     with more digits would yield to a {@link BigDecimal#scale()} that
 *     does not fit into a {@code int}-value.</li>
 *     <li>{@code BigDecimalString}: 1 to 536_870_919+4+10=536_870_933 characters,
 *     e.g. "-1.234567890....12345E-2147483647"</li>
 * </ul>
 * <p>
 * References:
 * <dl>
 *     <dt>Java SE 17 & JDK 17, JavaDoc, Class BigDecimal</dt>
 *     <dd><a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigDecimal.html#%3Cinit%3E(java.lang.String)">docs.oracle.com</a></dd>
 * </dl>
 */
public class JavaBigDecimalParser {


    /**
     * Don't let anyone instantiate this class.
     */
    private JavaBigDecimalParser() {

    }

    /**
     * Convenience method for calling {@link #parseBigDecimal(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static BigDecimal parseBigDecimal(CharSequence str) throws NumberFormatException {
        return parseBigDecimal(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@link CharSequence} and converts it
     * into a {@code double} value.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatingPointLiteral} in {@code str}
     * @param length the length of {@code FloatingPointLiteral} in {@code str}
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static BigDecimal parseBigDecimal(CharSequence str, int offset, int length) throws NumberFormatException {
        BigDecimal result = new JavaBigDecimalFromCharSequence().parseFloatingPointLiteral(str, offset, length);
        if (result == null) {
            throw new NumberFormatException("Illegal input");
        }
        return result;
    }

    /**
     * Convenience method for calling {@link #parseBigDecimal(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static BigDecimal parseBigDecimal(byte[] str) throws NumberFormatException {
        return parseBigDecimal(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code double} value.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first byte to parse
     * @param length The number of bytes to parse
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static BigDecimal parseBigDecimal(byte[] str, int offset, int length) throws NumberFormatException {
        BigDecimal result = new JavaBigDecimalFromByteArray().parseFloatingPointLiteral(str, offset, length);
        if (result == null) {
            throw new NumberFormatException("Illegal input");
        }
        return result;
    }

    /**
     * Convenience method for calling {@link #parseBigDecimal(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static BigDecimal parseBigDecimal(char[] str) throws NumberFormatException {
        return parseBigDecimal(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code double} value.
     * <p>
     * See {@link JavaBigDecimalParser} for the syntax of {@code FloatingPointLiteral}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed double value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static BigDecimal parseBigDecimal(char[] str, int offset, int length) throws NumberFormatException {
        BigDecimal result = new JavaBigDecimalFromCharArray().parseFloatingPointLiteral(str, offset, length);
        if (result == null) {
            throw new NumberFormatException("Illegal input");
        }
        return result;
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@link CharSequence} and converts it
     * into a bit pattern that encodes a {@code double} value.
     * <p>
     * Usage example:
     * <pre>
     *     long bitPattern = parseDoubleBits("3.14", 0, 4);
     *     if (bitPattern == -1L) {
     *         ...handle parse error...
     *     } else {
     *         double d = Double.longBitsToDouble(bitPattern);
     *     }
     * </pre>
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatingPointLiteral} in {@code str}
     * @param length the length of {@code FloatingPointLiteral} in {@code str}
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public static BigDecimal parseBigDecimalOrNull(CharSequence str, int offset, int length) {
        return new JavaBigDecimalFromCharSequence().parseFloatingPointLiteral(str, offset, length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a bit pattern that encodes a {@code double} value.
     * <p>
     * See {@link #parseBigDecimalOrNull(CharSequence, int, int)} for a usage example.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first byte to parse
     * @param length The number of bytes to parse
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public static BigDecimal parseBigDecimalOrNull(byte[] str, int offset, int length) {
        return new JavaBigDecimalFromByteArray().parseFloatingPointLiteral(str, offset, length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a bit pattern that encodes a {@code double} value.
     * <p>
     * See {@link #parseBigDecimalOrNull(CharSequence, int, int)} for a usage example.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public static BigDecimal parseBigDecimalOrNull(char[] str, int offset, int length) {
        return new JavaBigDecimalFromCharArray().parseFloatingPointLiteral(str, offset, length);
    }
}