/*
 * @(#)NumberFormatSymbols.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.text.DecimalFormatSymbols;
import java.util.Set;

public record NumberFormatSymbols(
        Set<Character> decimalSeparator,
        Set<Character> groupingSeparator,
        Set<String> exponentSeparator,
        Set<Character> minusSign,
        Set<Character> plusSign,
        Set<String> infinity,
        Set<String> nan,
        char zeroDigit
) {
    public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        return new NumberFormatSymbols(
                Set.of(symbols.getDecimalSeparator()),
                Set.of(symbols.getGroupingSeparator()),
                Set.of(symbols.getExponentSeparator()),
                Set.of(symbols.getMinusSign()),
                Set.of(),
                Set.of(symbols.getInfinity()),
                Set.of(symbols.getNaN()),
                symbols.getZeroDigit()
        );
    }

    public static NumberFormatSymbols fromDefault() {
        return new NumberFormatSymbols(
                Set.of('.'),
                Set.of(),
                Set.of("e", "E"),
                Set.of('-'),
                Set.of('+'),
                Set.of("Infinity"),
                Set.of("NaN"),
                '0'
        );
    }
}