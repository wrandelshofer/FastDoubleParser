/*
 * @(#)JavaDoubleParser.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormatSymbols;

/**
 * Parses a {@code double} value with a lenient syntax.
 * <p>
 * <b>Syntax</b>
 * <p>
 * <blockquote>
 * To be done
 * </blockquote>
 */
public class LenientDoubleParser {
    private final NumberFormatSymbols symbols;
    private LenientDoubleBitsFromCharSequence charSequenceParser;
    private LenientDoubleBitsFromCharArray charArrayParser;

    public LenientDoubleParser(NumberFormatSymbols symbols) {
        this.symbols = symbols;
    }

    private LenientDoubleBitsFromCharArray getCharArrayParser() {
        if (charArrayParser == null) {
            this.charArrayParser = new LenientDoubleBitsFromCharArray(symbols);
        }
        return charArrayParser;
    }

    private LenientDoubleBitsFromCharSequence getCharSequenceParser() {
        if (charSequenceParser == null) {
            this.charSequenceParser = new LenientDoubleBitsFromCharSequence(symbols);
        }
        return charSequenceParser;
    }

    public LenientDoubleParser(DecimalFormatSymbols symbols) {
        this(NumberFormatSymbols.fromDecimalFormatSymbols(symbols));
    }

    public LenientDoubleParser() {
        this(NumberFormatSymbols.fromDefault());
    }

    public double parseDouble(CharSequence str) {
        return Double.longBitsToDouble(getCharSequenceParser().parseFloatingPointLiteral(str, 0, str.length()));
    }

    public double parseDouble(CharSequence str, int offset, int length) {
        return Double.longBitsToDouble(getCharSequenceParser().parseFloatingPointLiteral(str, offset, length));
    }

    public double parseDouble(char[] str) {
        return Double.longBitsToDouble(getCharArrayParser().parseFloatingPointLiteral(str, 0, str.length));
    }

    public double parseDouble(char[] str, int offset, int length) {
        return Double.longBitsToDouble(getCharArrayParser().parseFloatingPointLiteral(str, offset, length));
    }
}
