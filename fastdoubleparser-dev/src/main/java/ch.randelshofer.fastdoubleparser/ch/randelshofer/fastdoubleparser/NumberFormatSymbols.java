/*
 * @(#)NumberFormatSymbols.java
 * Copyright © 2024 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;


import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines a set of symbols for {@link ConfigurableDoubleParser}.
 *
 * @param decimalSeparator  a set of decimal separator characters
 * @param groupingSeparator a set of grouping characters
 * @param exponentSeparator a set of exponent separator strings
 * @param minusSign         a set of minus sign characters
 * @param plusSign          a set of plus sign characters
 * @param infinity          a set of infinity strings
 * @param nan               a set of NaN strings
 * @param zeroDigit         a zero digit character
 */
public record NumberFormatSymbols(Set<Character> decimalSeparator, Set<Character> groupingSeparator,
                                  Set<String> exponentSeparator, Set<Character> minusSign, Set<Character> plusSign,
                                  Set<String> infinity, Set<String> nan, char zeroDigit) {

    public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        return new NumberFormatSymbols(
                Collections.singleton(symbols.getDecimalSeparator()),
                Collections.singleton(symbols.getGroupingSeparator()),
                Collections.singleton(symbols.getExponentSeparator()),
                Collections.singleton(symbols.getMinusSign()),
                Collections.emptySet(),
                Collections.singleton(symbols.getInfinity()),
                Collections.singleton(symbols.getNaN()),
                symbols.getZeroDigit()
        );
    }

    public static NumberFormatSymbols fromDefault() {
        return new NumberFormatSymbols(
                Collections.singleton('.'),
                Collections.emptySet(),
                new HashSet<>(Arrays.asList("e", "E")),
                Collections.singleton('-'),
                Collections.singleton('+'),
                Collections.singleton("Infinity"),
                Collections.singleton("NaN"),
                '0'
        );
    }

    @Override
    public String toString() {
        return "NumberFormatSymbols[" +
                "decimalSeparator=" + decimalSeparator + ", " +
                "groupingSeparator=" + groupingSeparator + ", " +
                "exponentSeparator=" + exponentSeparator + ", " +
                "minusSign=" + minusSign + ", " +
                "plusSign=" + plusSign + ", " +
                "infinity=" + infinity + ", " +
                "nan=" + nan + ", " +
                "zeroDigit=" + zeroDigit + ']';
    }

}