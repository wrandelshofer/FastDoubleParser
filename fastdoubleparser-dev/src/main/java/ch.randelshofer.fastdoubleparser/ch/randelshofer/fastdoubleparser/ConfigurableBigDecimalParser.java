/*
 * @(#)JavaBigDecimalParser.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Objects;

import static ch.randelshofer.fastdoubleparser.NumberFormatSymbolsInfo.isAscii;
import static ch.randelshofer.fastdoubleparser.NumberFormatSymbolsInfo.isDigitsTokensAscii;
import static ch.randelshofer.fastdoubleparser.NumberFormatSymbolsInfo.isMostlyAscii;

/**
 * Parses a floating point value with configurable {@link NumberFormatSymbols}.
 * <p>
 * <b>Syntax</b>
 * <p>
 * Leading {@link Character#FORMAT} characters in the string are ignored.
 * <blockquote>
 * <dl>
 * <dt><i>FloatingPointLiteral:</i></dt>
 * <dd><i>[Sign] NaN</i></dd>
 * <dd><i>[Sign] Infinity</i></dd>
 * <dd><i>[Sign] DecimalFloatingPointLiteral</i></dd>
 * </dl>
 *
 * <dl>
 * <dt><i>DecimalFloatingPointLiteral:</i>
 * <dd><i>DecSignificand [DecExponent]</i>
 * </dl>
 *
 * <dl>
 * <dt><i>DecSignificand:</i>
 * <dd><i>IntegerPart DecimalSeparator [FractionPart]</i>
 * <dd><i>DecimalSeparator FractionPart</i>
 * <dd><i>IntegerPart</i>
 * </dl>
 *
 * <dl>
 * <dt><i>IntegerPart:</i>
 * <dd><i>GroupedDigits</i>
 * </dl>
 *
 * <dl>
 * <dt><i>FractionPart:</i>
 * <dd><i>Digits</i>
 * </dl>
 *
 * <dl>
 * <dt><i>DecimalSeparator:</i>
 * <dd><i>(one of {@link NumberFormatSymbols#decimalSeparator()})</i>
 * </dl>
 *
 * <dl>
 * <dt><i>DecExponent:</i>
 * <dd><i>ExponentIndicator [Sign] Digits</i>
 * </dl>
 *
 * <dl>
 * <dt><i>ExponentIndicator:</i>
 * <dd><i>(one of {@link NumberFormatSymbols#exponentSeparator()})</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Sign:</i>
 * <dd><i>(one of {@link NumberFormatSymbols#minusSign()})</i>
 * <dd><i>(one of {@link NumberFormatSymbols#plusSign()})</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Digits:</i>
 * <dd><i>Digit {Digit}</i>
 * </dl>
 *
 * <dl>
 * <dt><i>GroupedDigits:</i>
 * <dd><i>DigitOrGrouping {DigitOrGrouping}</i>
 * </dl>
 *
 * <dl>
 * <dt><i>DigitOrGrouping:</i>
 * <dd><i>Digit</i>
 * <dd><i>Grouping</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Digit:</i>
 * <dd><i>(one of digits 0 through 9 starting with {@link NumberFormatSymbols#digits()})</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Sign:</i>
 * <dd><i>(one of {@link NumberFormatSymbols#groupingSeparator()})</i>
 * </dl>
 *
 * <dl>
 * <dt><i>NaN:</i>
 * <dd><i>(one of {@link NumberFormatSymbols#nan()})</i>
 * </dl>
 *
 * <dl>
 * <dt><i>Infinity:</i>
 * <dd><i>(one of {@link NumberFormatSymbols#infinity()})</i>
 * </dl>
 * </blockquote>
 * Maximal input length supported by this parser:
 * <ul>
 *     <li>{@code FloatingPointLiteral} with leading {@link Character#FORMAT} characters:
 *     {@link Integer#MAX_VALUE} - 4 = 2,147,483,643 characters.</li>
 * </ul>
 */
public final class ConfigurableBigDecimalParser {

    private ConfigurableBigDecimalFromByteArrayAscii byteArrayAsciiParser;
    private ConfigurableBigDecimalFromByteArrayUtf8 byteArrayUtf8Parser;

    private ConfigurableBigDecimalFromCharArray charArrayParser;

    private ConfigurableBigDecimalFromCharSequence charSequenceParser;
    private final NumberFormatSymbols symbols;
    private final boolean ignoreCase;
    private final boolean isAllSingleCharSymbolsAscii;
    private final boolean isDigitsAscii;
    private final boolean isAscii;

    private ConfigurableBigDecimalFromCharSequence getCharSequenceParser() {
        if (charSequenceParser == null) {
            this.charSequenceParser = new ConfigurableBigDecimalFromCharSequence(symbols, ignoreCase);
        }
        return charSequenceParser;
    }

    private ConfigurableBigDecimalFromCharArray getCharArrayParser() {
        if (charArrayParser == null) {
            this.charArrayParser = new ConfigurableBigDecimalFromCharArray(symbols, ignoreCase);
        }
        return charArrayParser;
    }

    private ConfigurableBigDecimalFromByteArrayAscii getByteArrayAsciiParser() {
        if (byteArrayAsciiParser == null) {
            this.byteArrayAsciiParser = new ConfigurableBigDecimalFromByteArrayAscii(symbols, ignoreCase);
        }
        return byteArrayAsciiParser;
    }

    private ConfigurableBigDecimalFromByteArrayUtf8 getByteArrayUtf8Parser() {
        if (byteArrayUtf8Parser == null) {
            this.byteArrayUtf8Parser = new ConfigurableBigDecimalFromByteArrayUtf8(symbols, ignoreCase);
        }
        return byteArrayUtf8Parser;
    }

    /**
     * Creates a new instance with the specified number format symbols.
     * <p>
     * The parser does not ignore case.
     *
     * @param symbols the number format symbols
     */
    public ConfigurableBigDecimalParser(NumberFormatSymbols symbols) {
        this(symbols, false);
    }

    /**
     * Creates a new instance with {@link NumberFormatSymbols#fromDefault()}
     * which does not ignore case.
     */
    public ConfigurableBigDecimalParser() {
        this(NumberFormatSymbols.fromDefault(), false);
    }

    /**
     * Creates a new instance with number format symbols derived
     * from the specified symbols by calling
     * {@link NumberFormatSymbols#fromDecimalFormatSymbols(DecimalFormatSymbols)}.
     * <p>
     * The parser does not ignore case.
     *
     * @param symbols the decimal format symbols
     */
    public ConfigurableBigDecimalParser(DecimalFormatSymbols symbols) {
        this(NumberFormatSymbols.fromDefault(), false);
    }

    /**
     * Creates a new instance with number format symbols derived
     * from the specified symbols by calling
     * {@link NumberFormatSymbols#fromDecimalFormatSymbols(DecimalFormatSymbols)}.
     * <p>
     *
     * @param symbols    the decimal format symbols
     * @param ignoreCase whether case should be ignored by the parser
     */
    public ConfigurableBigDecimalParser(DecimalFormatSymbols symbols, boolean ignoreCase) {
        this(NumberFormatSymbols.fromDefault(), ignoreCase);
    }

    /**
     * Creates a new instance with the specified number format symbols and case sensitivity.
     *
     * @param symbols    the number format symbols
     * @param ignoreCase whether case should be ignored by the parser
     */
    public ConfigurableBigDecimalParser(NumberFormatSymbols symbols, boolean ignoreCase) {
        Objects.requireNonNull(symbols, "symbols");
        this.symbols = symbols;
        this.ignoreCase = ignoreCase;
        this.isAllSingleCharSymbolsAscii = isMostlyAscii(symbols);
        this.isDigitsAscii = isDigitsTokensAscii(symbols);
        this.isAscii = isAscii(symbols);
    }

    /**
     * Convenience method for calling {@link #parseBigDecimal(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public BigDecimal parseBigDecimal(CharSequence str) throws NumberFormatException {
        return parseBigDecimal(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@link CharSequence} and converts it
     * into a {@link BigDecimal} value.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatingPointLiteral} in {@code str}
     * @param length the length of {@code FloatingPointLiteral} in {@code str}
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public BigDecimal parseBigDecimal(CharSequence str, int offset, int length) throws NumberFormatException {
        return getCharSequenceParser().parseBigDecimalString(str, offset, length);
    }

    /**
     * Convenience method for calling {@link #parseBigDecimal(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public BigDecimal parseBigDecimal(byte[] str) throws NumberFormatException {
        return parseBigDecimal(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@link BigDecimal} value.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first byte to parse
     * @param length The number of bytes to parse
     * @return the parsed value
     * @throws NullPointerException     if the string is null
     * @throws IllegalArgumentException if offset or length are illegal
     * @throws NumberFormatException    if the string can not be parsed successfully
     */
    public BigDecimal parseBigDecimal(byte[] str, int offset, int length) throws NumberFormatException {
        return getByteArrayAsciiParser().parseBigDecimalString(str, offset, length);
    }

    /**
     * Convenience method for calling {@link #parseBigDecimal(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string can not be parsed successfully
     */
    public BigDecimal parseBigDecimal(char[] str) throws NumberFormatException {
        return parseBigDecimal(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatingPointLiteral} from a {@code byte}-Array and converts it
     * into a {@code double} value.
     * <p>
     * See {@link ConfigurableBigDecimalParser} for the syntax of {@code FloatingPointLiteral}.
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
    public BigDecimal parseBigDecimal(char[] str, int offset, int length) throws NumberFormatException {
        return getCharArrayParser().parseBigDecimalString(str, offset, length);
    }
}