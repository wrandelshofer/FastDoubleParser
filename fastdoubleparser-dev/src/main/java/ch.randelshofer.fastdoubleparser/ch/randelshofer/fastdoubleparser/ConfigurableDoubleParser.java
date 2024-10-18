/*
 * @(#)ConfigurableDoubleParser.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormatSymbols;

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
public class ConfigurableDoubleParser {
    private final NumberFormatSymbols symbols;
    private ConfigurableDoubleBitsFromCharSequence charSequenceParser;
    private ConfigurableDoubleBitsFromCharArray charArrayParser;
    private final boolean ignoreCase;

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
        this.symbols = symbols;
        this.ignoreCase = ignoreCase;
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
        return Double.longBitsToDouble(getCharSequenceParser().parseFloatingPointLiteral(str, 0, str.length()));
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
        return Double.longBitsToDouble(getCharSequenceParser().parseFloatingPointLiteral(str, offset, length));
    }

    /**
     * Parses a double value from the specified char array.
     *
     * @param str a char array
     * @return a double value
     * @throws NumberFormatException if the provided char array could not be parsed
     */
    public double parseDouble(char[] str) {
        return Double.longBitsToDouble(getCharArrayParser().parseFloatingPointLiteral(str, 0, str.length));
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
        return Double.longBitsToDouble(getCharArrayParser().parseFloatingPointLiteral(str, offset, length));
    }
}
