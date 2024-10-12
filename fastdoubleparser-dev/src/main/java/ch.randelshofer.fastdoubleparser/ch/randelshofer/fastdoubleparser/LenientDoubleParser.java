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
    private final LenientDoubleBitsFromCharSequence pCharSequence;
    private final LenientDoubleBitsFromString pString;

    public LenientDoubleParser(NumberFormatSymbols symbols) {
        this.symbols = symbols;
        this.pCharSequence = new LenientDoubleBitsFromCharSequence(symbols);
        this.pString = new LenientDoubleBitsFromString(symbols);
    }

    public LenientDoubleParser(DecimalFormatSymbols symbols) {
        this(NumberFormatSymbols.fromDecimalFormatSymbols(symbols));
    }

    public LenientDoubleParser() {
        this(NumberFormatSymbols.fromDefault());
    }

    public double parseDouble(String str) {
        return Double.longBitsToDouble(pString.parseFloatingPointLiteral((String) str, 0, str.length()));
    }

    public double parseDouble(String str, int offset, int length) {
        return Double.longBitsToDouble(pString.parseFloatingPointLiteral((String) str, offset, length));
    }

    public double parseDouble(CharSequence str) {
        return Double.longBitsToDouble(pCharSequence.parseFloatingPointLiteral((String) str, 0, str.length()));
    }

    public double parseDouble(CharSequence str, int offset, int length) {
        return Double.longBitsToDouble(pCharSequence.parseFloatingPointLiteral((String) str, offset, length));
    }
}
