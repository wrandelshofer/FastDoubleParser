/*
 * @(#)ConfigurableDoubleParser.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormatSymbols;
import java.util.Objects;

import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.SYNTAX_ERROR;
import static ch.randelshofer.fastdoubleparser.AbstractNumberParser.SYNTAX_ERROR_BITS;
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
 * <dd><i>NaN [Sign]</i></dd>
 * <dd><i>Infinity [Sign]</i></dd>
 * <dd><i>DecimalFloatingPointLiteral</i></dd>
 * </dl>
 *
 * <dl>
 * <dt><i>DecimalFloatingPointLiteral:</i>
 * <dd><i>[Sign] DecSignificand [DecExponent]</i>
 * <dd><i>DecSignificand [Sign] [DecExponent]</i>
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
 * <dd><i>ExponentIndicator Digits [Sign]</i>
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
public final class ConfigurableDoubleParser {
    private final NumberFormatSymbols symbols;
    private ConfigurableDoubleBitsFromCharSequence charSequenceParser;
    private ConfigurableDoubleBitsFromCharArray charArrayParser;
    private final boolean ignoreCase;
    private final boolean isAllSingleCharSymbolsAscii;
    private final boolean isDigitsAscii;
    private final boolean isAscii;
    private ConfigurableDoubleBitsFromByteArrayAscii byteArrayAsciiParser;
    private ConfigurableDoubleBitsFromByteArrayUtf8 byteArrayUtf8Parser;

    /**
     * Creates a new instance with the specified number format symbols.
     * <p>
     * The parser does not ignore case.
     *
     * @param symbols the number format symbols
     */
    public ConfigurableDoubleParser(NumberFormatSymbols symbols) {
        this(symbols, false);
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
    public ConfigurableDoubleParser(DecimalFormatSymbols symbols) {
        this(symbols, false);
    }

    /**
     * Creates a new instance with the specified number format symbols and case sensitivity.
     *
     * @param symbols    the number format symbols
     * @param ignoreCase whether case should be ignored by the parser
     */
    public ConfigurableDoubleParser(NumberFormatSymbols symbols, boolean ignoreCase) {
        Objects.requireNonNull(symbols, "symbols");
        this.symbols = symbols;
        this.ignoreCase = ignoreCase;
        this.isAllSingleCharSymbolsAscii = isMostlyAscii(symbols);
        this.isDigitsAscii = isDigitsTokensAscii(symbols);
        this.isAscii = isAscii(symbols);
    }

    /**
     * Gets the number format symbols of this parser.
     *
     * @return the number format symbols
     */
    public NumberFormatSymbols getNumberFormatSymbols() {
        return symbols;
    }

    /**
     * Returns true of this parser ignores case.
     *
     * @return true if case is ignored
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }



    /**
     * Creates a new instance with decimal format symbols and case sensitivity.
     * <p>
     * The number format symbols are derived
     * from the specified decimal format symbols by calling
     * {@link NumberFormatSymbols#fromDecimalFormatSymbols(DecimalFormatSymbols)}.
     *
     * @param symbols    the decimal format symbols
     * @param ignoreCase whether case should be ignored by the parser
     */
    public ConfigurableDoubleParser(DecimalFormatSymbols symbols, boolean ignoreCase) {
        this(NumberFormatSymbols.fromDecimalFormatSymbols(symbols), ignoreCase);
    }

    /**
     * Creates a new instance with {@link NumberFormatSymbols#fromDefault()}
     * which does not ignore case.
     */
    public ConfigurableDoubleParser() {
        this(NumberFormatSymbols.fromDefault(), false);
    }

    private ConfigurableDoubleBitsFromCharArray getCharArrayParser() {
        if (charArrayParser == null) {
            this.charArrayParser = new ConfigurableDoubleBitsFromCharArray(symbols, ignoreCase);

        }
        return charArrayParser;
    }

    private ConfigurableDoubleBitsFromByteArrayAscii getByteArrayAsciiParser() {
        if (byteArrayAsciiParser == null) {
            this.byteArrayAsciiParser = new ConfigurableDoubleBitsFromByteArrayAscii(symbols, ignoreCase);

        }
        return byteArrayAsciiParser;
    }

    private ConfigurableDoubleBitsFromByteArrayUtf8 getByteArrayUtf8Parser() {
        if (byteArrayUtf8Parser == null) {
            this.byteArrayUtf8Parser = new ConfigurableDoubleBitsFromByteArrayUtf8(symbols, ignoreCase);

        }
        return byteArrayUtf8Parser;
    }

    private ConfigurableDoubleBitsFromCharSequence getCharSequenceParser() {
        if (charSequenceParser == null) {
            this.charSequenceParser = new ConfigurableDoubleBitsFromCharSequence(symbols, ignoreCase);
        }
        return charSequenceParser;
    }

    /**
     * Parses a double value from the specified char sequence.
     *
     * @param str a char sequence
     * @return a double value
     * @throws NumberFormatException if the provided char sequence could not be parsed
     */
    public double parseDouble(CharSequence str) {
        return parseDouble(str, 0, str.length());
    }

    /**
     * Parses a double value from a substring of the specified char sequence.
     *
     * @param str    a char sequence
     * @param offset the start offset of the substring
     * @param length the length of the substring
     * @throws NumberFormatException if the provided char sequence could not be parsed
     */
    public double parseDouble(CharSequence str, int offset, int length) {
        long bitPattern = getCharSequenceParser().parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == SYNTAX_ERROR_BITS) throw new NumberFormatException(SYNTAX_ERROR);
        return Double.longBitsToDouble(bitPattern);
    }

    /**
     * Parses a double value from the specified char array.
     *
     * @param str a char array
     * @return a double value
     * @throws NumberFormatException if the provided char array could not be parsed
     */
    public double parseDouble(char[] str) {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a double value from a substring of the specified char array.
     *
     * @param str    a char array
     * @param offset the start offset of the substring
     * @param length the length of the substring
     * @throws NumberFormatException if the provided char array could not be parsed
     */
    public double parseDouble(char[] str, int offset, int length) {
        long bitPattern = getCharArrayParser().parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == SYNTAX_ERROR_BITS) throw new NumberFormatException(SYNTAX_ERROR);
        return Double.longBitsToDouble(bitPattern);
    }

    /**
     * Parses a double value from the specified byte array.
     *
     * @param str a byte array
     * @return a double value
     * @throws NumberFormatException if the provided char array could not be parsed
     */
    public double parseDouble(byte[] str) {
        return parseDouble(str, 0, str.length);
    }

    /**
     * Parses a double value from a substring of the specified byte array.
     *
     * @param str    a byte array
     * @param offset the start offset of the substring
     * @param length the length of the substring
     * @throws NumberFormatException if the provided char array could not be parsed
     */
    public double parseDouble(byte[] str, int offset, int length) {
        long bitPattern;
        if (isAscii || !ignoreCase && isAllSingleCharSymbolsAscii) {
            bitPattern = getByteArrayAsciiParser().parseFloatingPointLiteral(str, offset, length);
        } else if (isDigitsAscii) {
            bitPattern = getByteArrayUtf8Parser().parseFloatingPointLiteral(str, offset, length);
        } else {
            Utf8Decoder.Result result = Utf8Decoder.decode(str, offset, length);
            bitPattern = getCharArrayParser().parseFloatingPointLiteral(result.chars(), 0, result.length());
        }
        if (bitPattern == SYNTAX_ERROR_BITS) throw new NumberFormatException(SYNTAX_ERROR);
        return Double.longBitsToDouble(bitPattern);
    }
}
